package com.github.mizool.core.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Joins a stream of futures, returning a single joint future. Accumulation or reduction of results is intentionally not
 * supported, so all futures must be of (or converted to) type {@link Void} (see below).<br>
 * <br>
 * The stream is consumed as fast as possible without the number of incomplete futures exceeding {@code
 * concurrencyLimit}. The stream consumer is executed by the given {@link ExecutorService}.<br>
 * <br>
 * <h3>Exceptional completion of futures</h3>
 * <ul>
 * <li>When one or more of the futures inside the stream complete exceptionally, so does the joint future. After that,
 * one more future will be consumed from the stream, but the joint future will not wait for it to complete.</li>
 * <li>The {@link Throwable} result of the joint future will be that of the first future to complete exceptionally;
 * others will be lost.</li>
 * <li>As with other futures, the throwable result of a computation will be wrapped inside an
 * {@link ExecutionException}, e.g. when invoking {@link Future#get() get()}. Note that this happens regardless of the
 * type of throwable (checked exception, runtime exception, error).</li>
 * </ul>
 * <br>
 * <h3>Exceptions thrown by the original stream</h3>
 * If the original stream throws an exception instead of producing a future, the joint future will behave the same way
 * as with exceptional completion of futures: the exception will be wrapped inside an {@link ExecutionException}, e.g.
 * when invoking {@link Future#get() get()}.<br>
 * <br>
 * <h3>Usage examples</h3>
 * <pre>{@code
 * Stream<CompletableFuture<Void>> completables;
 * CompletableFuture<Void> jointFuture = FutureStreamJoiner.completable().join(completables, concurrencyLimit, executorService);}</pre>
 * <pre>{@code
 * Stream<ListenableFuture<Void>> listenables;
 * ListenableFuture<Void> jointFuture = FutureStreamJoiner.listenable().join(listenables, concurrencyLimit, executorService);}</pre>
 * <br>
 * As this class can't handle a future's result by design, it only accepts futures without result. If the futures in
 * the stream <i>do</i> contain results, the intention to discard those results can be documented by using
 * {@link Futures#toVoidResult(CompletableFuture)} or {@link Futures#toVoidResult(ListenableFuture)}:<br>
 * <pre>{@code
 * Stream<CompletableFuture<MyPojo>> resultFutures;
 * Stream<CompletableFuture<Void>> voidFutures = resultFutures.map(Futures::toVoidResult);
 * CompletableFuture<Void> jointFuture = FutureStreamJoiner.completable().join(voidFutures, concurrencyLimit, executorService);}</pre>
 * <pre>{@code
 * Stream<ListenableFuture<MyPojo>> resultFutures;
 * Stream<ListenableFuture<Void>> voidFutures = resultFutures.map(Futures::toVoidResult);
 * CompletableFuture<Void> jointFuture = FutureStreamJoiner.listenable().join(voidFutures, concurrencyLimit, executorService);}</pre>
 */
public final class FutureStreamJoiner
{
    private FutureStreamJoiner()
    {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Provides fluent syntax for joining {@link ListenableFuture ListenableFutures}. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Listenable
    {
        /**
         * Returns a future that will complete normally once all futures in the given stream have completed normally.
         * <br>
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

            return Futures.toVoidResult(MoreExecutors.listeningDecorator(executorService)
                .submit(consumeStream(results)));
        }
    }

    /**
     * Provides fluent syntax for joining completable futures. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Completable
    {
        /**
         * Returns a future that will complete normally once all futures in the given stream have completed normally.
         * <br>
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

    @RequiredArgsConstructor
    private static final class StreamConsumingWorker implements Runnable
    {
        private final Stream<Void> stream;

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

    /**
     * Entry point for joining listenable futures. <br>
     * <br>
     * See {@link FutureStreamJoiner} for details.
     *
     * @return fluent syntax
     */
    public static Listenable listenable()
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
    public static Completable completable()
    {
        return new Completable();
    }

    private static void verifyConcurrencyLimit(int concurrencyLimit)
    {
        if (concurrencyLimit <= 0)
        {
            throw new IllegalArgumentException("Concurrency limit must be greater than 0");
        }
    }

    private static Runnable consumeStream(Stream<Void> stream)
    {
        return new StreamConsumingWorker(stream);
    }
}
