/*
 * Copyright 2018-2020 incub8 Software Labs GmbH
 * Copyright 2018-2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Buffers a {@link java.util.stream.Stream} of {@link ListenableFuture}s and provides the results as a new
 * {@link java.util.stream.Stream}. <br>
 * <br>
 * Futures are collected as fast as possible without the sum of concurrently running futures and available results
 * exceeding {@code bufferSize}.<br>
 * The new stream contains the results in the completion order of the futures. It blocks while no results are available
 * and there are still futures to wait for.<br>
 * Any {@link Throwable} thrown by one of the incoming futures is wrapped in an {@link UncheckedExecutionException} and
 * thrown while supplying the new stream with values.<br>
 * <br>
 * This class is intended to be used as follows:<br>
 * <pre>{@code
 * Stream<Completable<V>> completables;
 * Stream<V> values2 = BufferedStreamAdapter.completable().adapt(completables, bufferSize, executorService);}</pre>
 * <pre>{@code
 * Stream<ListenableFuture<V>> listenables;
 * Stream<V> values1 = BufferedStreamAdapter.listenable().adapt(listenables, bufferSize, executorService);}</pre>
 */
public final class BufferedStreamAdapter<F, V>
{
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Listenable
    {
        public <V> Stream<V> adapt(
            Stream<ListenableFuture<V>> futures, int bufferSize, ExecutorService executorService)
        {
            verifyBufferSize(bufferSize);

            return new BufferedStreamAdapter<ListenableFuture<V>, V>(futures,
                bufferSize,
                executorService,
                this::addListener).adapt();
        }

        private <E> void addListener(ListenableFuture<E> future, Listener<E> listener)
        {
            Futures.addCallback(future, new ListenableFutureCallback<>(listener), MoreExecutors.directExecutor());
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Completable
    {
        public <E> Stream<E> adapt(
            Stream<CompletableFuture<E>> futures, int bufferSize, ExecutorService executorService)
        {
            verifyBufferSize(bufferSize);

            return new BufferedStreamAdapter<CompletableFuture<E>, E>(futures,
                bufferSize,
                executorService,
                this::addListener).adapt();
        }

        private <E> void addListener(CompletableFuture<E> future, Listener<E> listener)
        {
            future.whenComplete(listener);
        }
    }

    public static Listenable listenable()
    {
        return new Listenable();
    }

    public static Completable completable()
    {
        return new Completable();
    }

    /**
     * @deprecated Use {@link #listenable()} and {@link Listenable#adapt(Stream, int, ExecutorService)} instead.
     */
    @Deprecated
    public static <V> Stream<V> adapt(
        Stream<ListenableFuture<V>> futures, int bufferSize, ExecutorService executorService)
    {
        return BufferedStreamAdapter.listenable().adapt(futures, bufferSize, executorService);
    }

    public static void verifyBufferSize(int bufferSize)
    {
        if (bufferSize <= 0)
        {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
    }

    private static class ValueHolder<V>
    {
        private final V value;
        private final Throwable throwable;

        public ValueHolder(V value)
        {
            this.value = value;
            throwable = null;
        }

        public ValueHolder(Throwable throwable)
        {
            value = null;
            this.throwable = throwable;
        }

        public V obtain()
        {
            if (throwable instanceof CompletionException)
            {
                throw (CompletionException) throwable;
            }
            else if (throwable != null)
            {
                throw new UncheckedExecutionException(throwable);
            }
            return value;
        }
    }

    @Value
    @Getter(value = AccessLevel.NONE)
    private static class ListenableFutureCallback<E> implements FutureCallback<E>
    {
        BiConsumer<E, Throwable> biConsumer;

        @Override
        public void onSuccess(E value)
        {
            biConsumer.accept(value, null);
        }

        @Override
        public void onFailure(Throwable t)
        {
            biConsumer.accept(null, t);
        }
    }

    private BooleanSupplier capacityAvailable()
    {
        return () -> runningFutures.get() + results.size() < bufferSize;
    }

    private class BlockingSpliterator implements Spliterator<V>
    {
        @Override
        public boolean tryAdvance(Consumer<? super V> action)
        {
            ValueHolder<V> valueHolder = Threads.waitUntilAndDo(resultOrCompletion(), () -> {
                ValueHolder<V> removedValue = null;
                if (resultsAvailable())
                {
                    removedValue = results.remove();
                    semaphore.notifyAll();
                }
                return removedValue;
            }, semaphore);

            boolean valueEmitted = false;
            if (valueHolder != null)
            {
                V value = valueHolder.obtain();
                action.accept(value);
                valueEmitted = true;
            }
            return valueEmitted;
        }

        private BooleanSupplier resultOrCompletion()
        {
            return () -> resultsAvailable() || completed();
        }

        private boolean resultsAvailable()
        {
            return !results.isEmpty();
        }

        private boolean completed()
        {
            return streamDepleted.get() && runningFutures.get() == 0;
        }

        @Override
        public Spliterator<V> trySplit()
        {
            return null;
        }

        @Override
        public long estimateSize()
        {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics()
        {
            return Spliterator.IMMUTABLE;
        }
    }

    private final Stream<F> futures;
    private final int bufferSize;
    private final ExecutorService executorService;
    private final ListenerAdder<F, V> listenerAdder;

    private final Queue<ValueHolder<V>> results;
    private final Object semaphore = new Object();
    private final AtomicInteger runningFutures = new AtomicInteger();

    private final AtomicBoolean streamDepleted = new AtomicBoolean();

    private BufferedStreamAdapter(
        Stream<F> futures, int bufferSize, ExecutorService executorService, ListenerAdder<F, V> listenerAdder)
    {
        this.futures = futures;
        this.bufferSize = bufferSize;
        this.executorService = executorService;
        this.listenerAdder = listenerAdder;
        this.results = new LinkedList<>();
    }

    @FunctionalInterface
    private interface ListenerAdder<T, E> extends BiConsumer<T, Listener<E>>
    {
    }

    @FunctionalInterface
    private interface Listener<E> extends BiConsumer<E, Throwable>
    {
    }

    private Stream<V> adapt()
    {
        executorService.submit(() -> {
            try
            {
                futures.forEach(this::consumeFuture);
            }
            catch (RuntimeException e)
            {
                synchronized (semaphore)
                {
                    results.add(new ValueHolder<>(e));
                    semaphore.notifyAll();
                }
            }
            synchronized (semaphore)
            {
                streamDepleted.set(true);
                semaphore.notifyAll();
            }
        });
        return StreamSupport.stream(new BlockingSpliterator(), false);
    }

    private void consumeFuture(F future)
    {
        Runnable accept = () -> {
            runningFutures.incrementAndGet();

            listenerAdder.accept(future, this::handleFutureResult);
        };
        Threads.doAndWaitUntil(accept, capacityAvailable(), semaphore);
    }

    private void handleFutureResult(V value, Throwable throwable)
    {
        synchronized (semaphore)
        {
            runningFutures.decrementAndGet();

            ValueHolder<V> valueHolder;
            if (throwable != null)
            {
                valueHolder = new ValueHolder<>(throwable);
            }
            else
            {
                valueHolder = new ValueHolder<>(value);
            }
            results.add(valueHolder);

            semaphore.notifyAll();
        }
    }
}