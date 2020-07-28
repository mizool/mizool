/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.UtilityClass;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Joins a stream of futures, returning a single joint future. Accumulation or reduction of results is intentionally not
 * supported, so all futures must be of type {@link Void}.<br>
 * <br>
 * The stream is consumed as fast as possible without the number of incomplete futures exceeding {@code
 * concurrencyLimit}. The stream consumer is executed by the given {@link ExecutorService}.<br>
 * <br>
 * <h3>Exceptional completion</h3>
 * <ul>
 * <li>When one or more of the futures inside the stream complete exceptionally, so does the joint future. After that,
 * one more future will be consumed from the stream, but the joint future will not wait for it to complete.</li>
 * <li>The {@link Throwable} result of the joint future will be that of the first future to complete exceptionally;
 * others will be lost.</li>
 * <li>As with other futures, the original throwable will be wrapped inside an {@link ExecutionException}, e.g. when
 * invoking {@link Future#get() get()}. Note that this happens regardless of the type of throwable (checked
 * exception, runtime exception, error).</li>
 * </ul>
 * <h3>Examples</h3>
 * This class is intended to be used as follows:<br>
 * <pre>{@code
 * Stream<CompletableFuture<Void>> completables;
 * CompletableFuture<Void> join2 = FutureStreamJoiner.completable().join(completables, concurrencyLimit, executorService);}</pre>
 * <pre>{@code
 * Stream<ListenableFuture<Void>> listenables;
 * ListenableFuture<Void> join1 = FutureStreamJoiner.listenable().join(listenables, concurrencyLimit, executorService);}</pre>
 */
@UtilityClass
public class FutureStreamJoiner
{
    /**
     * Provides fluent syntax for joining {@link ListenableFuture ListenableFutures}. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Listenable
    {
        /**
         * Returns a future that will complete normally once all futures in the given stream have completed
         * normally. <br>
         * <br>
         * See {@link FutureStreamJoiner} for details.
         *
         * @param listenableFutures the futures to join
         * @param concurrencyLimit the maximum number of incomplete futures at any point in time
         * @param executorService where to execute the stream consumer
         */
        public ListenableFuture<Void> join(
            Stream<ListenableFuture<Void>> listenableFutures, int concurrencyLimit, ExecutorService executorService)
        {
            verifyConcurrencyLimit(concurrencyLimit);

            Stream<Void> results = BufferedStreamAdapter.listenable()
                .adapt(listenableFutures, concurrencyLimit, executorService);

            return (ListenableFuture<Void>) MoreExecutors.listeningDecorator(executorService)
                .submit(consumeStream(results));
        }
    }

    /**
     * Provides fluent syntax for joining completable futures. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class Completable
    {
        /**
         * Returns a future that will complete normally once all futures in the given stream have completed
         * normally. <br>
         * <br>
         * See {@link FutureStreamJoiner} for details.
         *
         * @param completableFutures the futures to join
         * @param concurrencyLimit the maximum number of incomplete futures at any point in time
         * @param executorService where to execute the stream consumer
         */
        public CompletableFuture<Void> join(
            Stream<CompletableFuture<Void>> completableFutures, int concurrencyLimit, ExecutorService executorService)
        {
            verifyConcurrencyLimit(concurrencyLimit);

            Stream<Void> results = BufferedStreamAdapter.completable()
                .adapt(completableFutures, concurrencyLimit, executorService);

            return CompletableFuture.runAsync(consumeStream(results), executorService);
        }
    }

    /**
     * Entry point for joining listenable futures. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     *
     * @return fluent syntax
     */
    public Listenable listenable()
    {
        return new Listenable();
    }

    /**
     * Entry point for joining completable futures. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     *
     * @return fluent syntax
     */
    public Completable completable()
    {
        return new Completable();
    }

    private void verifyConcurrencyLimit(int concurrencyLimit)
    {
        if (concurrencyLimit <= 0)
        {
            throw new IllegalArgumentException("Concurrency limit must be greater than 0");
        }
    }

    private Runnable consumeStream(Stream<Void> stream)
    {
        return new StreamConsumingWorker(stream);
    }

    @Value
    @Getter(value = AccessLevel.NONE)
    private class StreamConsumingWorker implements Runnable
    {
        Stream<Void> stream;

        @Override
        @SneakyThrows
        public void run()
        {
            try
            {
                stream.forEach(whatever -> {
                });
            }
            catch (UncheckedExecutionException t)
            {
                throw tryUnwrap(t);
            }
        }

        private Throwable tryUnwrap(Throwable t)
        {
            Throwable cause = t.getCause();
            if (cause != null)
            {
                return cause;
            }

            return t;
        }
    }
}
