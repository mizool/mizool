/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Buffers a {@link java.util.stream.Stream} of {@link ListenableFuture}s and provides the results as a new
 * {@link java.util.stream.Stream}.<br>
 * <br>
 * Futures are collected as fast as possible without the sum of concurrently running futures and available results
 * exceeding {@code bufferSize}.<br>
 * The new stream contains the results in the completion order of the futures. It blocks while no results are available
 * and there are still futures to wait for.<br>
 * Any {@link Throwable} thrown by one of the incoming futures is wrapped in an {@link UncheckedExecutionException} and
 * thrown while supplying the new stream with values.<br>
 * <br>
 * This buffer is intended to be used like so:<br>
 * <pre>{@code
 * Stream<ListenableFuture<V>> futureStream;
 * Stream<V> resultStream = BufferedStreamAdapter.adapt(futureStream, bufferSize, executorService);}</pre>
 */
public class BufferedStreamAdapter<V>
{
    public static <V> Stream<V> adapt(
        Stream<ListenableFuture<V>> futures, int bufferSize, ExecutorService executorService)
    {
        if (bufferSize <= 0)
        {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }
        return new BufferedStreamAdapter<>(futures, bufferSize, executorService).adapt();
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
            if (throwable != null)
            {
                throw new UncheckedExecutionException(throwable);
            }
            return value;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private class Callback implements FutureCallback<V>
    {
        @Override
        public void onSuccess(V value)
        {
            synchronized (semaphore)
            {
                runningFutures.decrementAndGet();
                results.add(new ValueHolder<>(value));
                semaphore.notifyAll();
            }
        }

        @Override
        public void onFailure(Throwable t)
        {
            synchronized (semaphore)
            {
                runningFutures.decrementAndGet();
                results.add(new ValueHolder<>(t));
                semaphore.notifyAll();
            }
        }
    }

    private class BlockingConsumer implements Consumer<ListenableFuture<V>>
    {
        @Override
        public void accept(ListenableFuture<V> future)
        {
            synchronized (semaphore)
            {
                runningFutures.incrementAndGet();
                Futures.addCallback(future, new Callback());
                waitWhileBufferSizeReached();
            }
        }

        private void waitWhileBufferSizeReached()
        {
            while (runningFutures.get() + results.size() >= bufferSize)
            {
                Threads.wait(semaphore);
            }
        }
    }

    private class BlockingSpliterator implements Spliterator<V>
    {
        @Override
        public boolean tryAdvance(Consumer<? super V> action)
        {
            ValueHolder<V> valueHolder = null;
            synchronized (semaphore)
            {
                waitForResultOrCompletion();

                if (!results.isEmpty())
                {
                    valueHolder = results.remove();
                    semaphore.notifyAll();
                }
            }

            boolean valueEmitted = false;
            if (valueHolder != null)
            {
                V value = valueHolder.obtain();
                action.accept(value);
                valueEmitted = true;
            }
            return valueEmitted;
        }

        private void waitForResultOrCompletion()
        {
            while (results.isEmpty() && hasUpcomingFutures())
            {
                Threads.wait(semaphore);
            }
        }

        private boolean hasUpcomingFutures()
        {
            return !streamDepleted.get() || runningFutures.get() > 0;
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

    private final Stream<ListenableFuture<V>> futures;
    private final int bufferSize;
    private final ExecutorService executorService;
    private final Queue<ValueHolder<V>> results;

    private final Object semaphore = new Object();
    private final AtomicInteger runningFutures = new AtomicInteger();
    private final AtomicBoolean streamDepleted = new AtomicBoolean();

    private BufferedStreamAdapter(Stream<ListenableFuture<V>> futures, int bufferSize, ExecutorService executorService)
    {
        this.futures = futures;
        this.bufferSize = bufferSize;
        this.executorService = executorService;
        this.results = new LinkedList<>();
    }

    private Stream<V> adapt()
    {
        executorService.submit(() -> {
            futures.forEach(new BlockingConsumer());
            synchronized (semaphore)
            {
                streamDepleted.set(true);
                semaphore.notifyAll();
            }
        });
        return StreamSupport.stream(new BlockingSpliterator(), false);
    }
}