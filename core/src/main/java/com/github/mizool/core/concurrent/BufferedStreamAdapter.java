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
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Buffers a stream of futures and provides the results as a new stream. <br>
 * <br>
 * Futures are collected as fast as possible without the sum of concurrently running futures and available results
 * exceeding {@code bufferSize}.<br>
 * <br>
 * The new stream contains the results in the completion order of the futures. It blocks while no results are available
 * and there are still futures to wait for.<br>
 * <br>
 * <h3>Exceptional completion of futures</h3>
 * When one or more of the futures inside the stream complete exceptionally, the new stream will throw that exception
 * when an attempt is made to consume the next result. The exception is thrown as is (i.e. not wrapped in
 * {@link java.util.concurrent.ExecutionException} as with {@link Future#get()}). Basically, the new stream behaves as
 * if all computations were performed in a synchronous manner.<br>
 * <br>
 * <h3>Exceptions thrown by the original stream</h3>
 * If the original stream throws an exception instead of producing a future, the new stream will behave the same way
 * as with exceptional completion of futures: the exception will be thrown when an attempt is made to consume the next
 * result.<br>
 * <br>
 * <h3>Usage examples</h3>
 * <pre>{@code
 * Stream<Completable<V>> completables;
 * Stream<V> values2 = BufferedStreamAdapter.completable().adapt(completables, bufferSize, executorService);}</pre>
 * <pre>{@code
 * Stream<ListenableFuture<V>> listenables;
 * Stream<V> values1 = BufferedStreamAdapter.listenable().adapt(listenables, bufferSize, executorService);}</pre>
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BufferedStreamAdapter<F, V>
{
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Listenable
    {
        public <E> Stream<E> adapt(
            Stream<ListenableFuture<E>> futures, int bufferSize, ExecutorService executorService)
        {
            verifyBufferSize(bufferSize);

            return new BufferedStreamAdapter<ListenableFuture<E>, E>(futures,
                bufferSize,
                executorService,
                this::addListener,
                this::unwrapException).adapt();
        }

        private <E> void addListener(ListenableFuture<E> future, Listener<E> listener)
        {
            Futures.addCallback(future, new ListenableFutureCallback<>(listener), MoreExecutors.directExecutor());
        }

        private Throwable unwrapException(@NonNull Throwable throwable)
        {
            return throwable instanceof UncheckedExecutionException ? throwable.getCause() : throwable;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Completable
    {
        public <E> Stream<E> adapt(
            Stream<CompletableFuture<E>> futures, int bufferSize, ExecutorService executorService)
        {
            verifyBufferSize(bufferSize);

            return new BufferedStreamAdapter<CompletableFuture<E>, E>(futures,
                bufferSize,
                executorService,
                this::addListener,
                this::unwrapException).adapt();
        }

        private <E> void addListener(CompletableFuture<E> future, Listener<E> listener)
        {
            future.whenComplete(listener);
        }

        private Throwable unwrapException(@NonNull Throwable throwable)
        {
            return throwable instanceof CompletionException ? throwable.getCause() : throwable;
        }
    }

    private static final class ValueHolder<E>
    {
        private final E value;
        private final Throwable throwable;

        public ValueHolder(E value)
        {
            this.value = value;
            throwable = null;
        }

        public ValueHolder(Throwable throwable)
        {
            value = null;
            this.throwable = throwable;
        }

        @SneakyThrows
        public E obtain()
        {
            if (throwable != null)
            {
                throw throwable;
            }
            return value;
        }
    }

    @RequiredArgsConstructor
    private static final class ListenableFutureCallback<E> implements FutureCallback<E>
    {

        private final Listener<E> listener;

        @Override
        public void onSuccess(E value)
        {
            listener.accept(value, null);
        }

        @Override
        public void onFailure(Throwable t)
        {
            listener.accept(null, t);
        }
    }

    @FunctionalInterface
    private interface ListenerAdder<T, E> extends BiConsumer<T, Listener<E>>
    {

    }

    @FunctionalInterface
    private interface Listener<E> extends BiConsumer<E, Throwable>
    {

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
    public static <E> Stream<E> adapt(
        Stream<ListenableFuture<E>> futures, int bufferSize, ExecutorService executorService)
    {
        return BufferedStreamAdapter.listenable()
            .adapt(futures, bufferSize, executorService);
    }

    private static void verifyBufferSize(int bufferSize)
    {
        if (bufferSize <= 0)
        {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
    }

    private final class BlockingSpliterator implements Spliterator<V>
    {
        @Override
        public boolean tryAdvance(Consumer<? super V> action)
        {
            ValueHolder<V> valueHolder = synchronizer.buildSequenceOf()
                .sleepUntil(resultOrCompletion())
                .get(() -> {
                    if (resultsAvailable())
                    {
                        return results.remove();
                    }
                    return null;
                })
                .andWakeOthersIf(Objects::nonNull)
                .invokeSequence();

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
    private final UnaryOperator<Throwable> exceptionUnwrapper;

    private final Queue<ValueHolder<V>> results = new LinkedList<>();
    private final Synchronizer synchronizer = new Synchronizer();
    private final AtomicInteger runningFutures = new AtomicInteger();
    private final AtomicBoolean streamDepleted = new AtomicBoolean();

    private Stream<V> adapt()
    {
        executorService.submit(this::consumeStream);
        return StreamSupport.stream(new BlockingSpliterator(), false);
    }

    private void consumeStream()
    {
        try
        {
            futures.forEach(this::consumeFuture);
        }
        catch (@SuppressWarnings("java:S1181") Throwable throwable)
        {
            /*
             * Unlike convertFutureResultToValueHolder(), we are dealing with "synchronous" exceptions here that we
             * don't need to unwrap.
             */
            synchronizer.buildSequenceOf()
                .run(() -> results.add(new ValueHolder<>(throwable)))
                .andWakeOthers()
                .invokeSequence();
        }

        synchronizer.buildSequenceOf()
            .run(() -> streamDepleted.set(true))
            .andWakeOthers()
            .invokeSequence();
    }

    private void consumeFuture(F future)
    {
        synchronizer.buildSequenceOf()
            .run(() -> {
                runningFutures.incrementAndGet();
                listenerAdder.accept(future, this::handleFutureResult);
            })
            .thenSleepUntil(capacityAvailable())
            .invokeSequence();
    }

    private void handleFutureResult(V value, Throwable throwable)
    {
        synchronizer.buildSequenceOf()
            .run(() -> {
                runningFutures.decrementAndGet();

                ValueHolder<V> valueHolder = convertFutureResultToValueHolder(value, throwable);
                results.add(valueHolder);
            })
            .andWakeOthers()
            .invokeSequence();
    }

    private ValueHolder<V> convertFutureResultToValueHolder(V value, Throwable throwable)
    {
        ValueHolder<V> valueHolder;
        if (throwable != null)
        {
            Throwable cause = exceptionUnwrapper.apply(throwable);
            valueHolder = new ValueHolder<>(cause);
        }
        else
        {
            valueHolder = new ValueHolder<>(value);
        }
        return valueHolder;
    }

    private BooleanSupplier capacityAvailable()
    {
        return () -> runningFutures.get() + results.size() < bufferSize;
    }
}