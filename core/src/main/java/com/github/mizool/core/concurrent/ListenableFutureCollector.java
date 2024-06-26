package com.github.mizool.core.concurrent;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Collects a {@link java.util.stream.Stream} of {@link ListenableFuture}s of {@link Void} into one.<br>
 * <br>
 * Futures are collected as fast as possible without the amount of concurrently running futures exceeding {@code
 * maximumConcurrentFutures}. The resulting future allows to wait for all futures to complete.<br> Any {@link
 * java.util.concurrent.ExecutionException} thrown by one of the incoming futures is either thrown during collection or
 * forwarded to the result future if the consumer was already finished.<br>
 * <br>
 * This collector cannot be used for parallel streams as it would not be able to enforce the limit of {@code
 * maximumConcurrentFutures}.<br>
 * <br>
 * This collector is intended to be used like so:<br>
 * <pre>{@code
 * Stream<ListenableFuture<Void>> stream;
 * stream.collect(ListenableFutureCollector.concurrent(maximumConcurrentFutures)).get();}</pre>
 * <br>
 * As the collector can't handle a future's result by design, it only accepts futures without result. If the futures in
 * the stream <i>do</i> contain results, the intention to discard those results can be documented by using the {@link
 * ResultVoidingFuture}:<br>
 * <pre>{@code
 * Stream<ListenableFuture<MyPojo>> stream;
 * stream.map(ResultVoidingFuture::new).collect(ListenableFutureCollector.concurrent(maximumConcurrentFutures)).get();}</pre>
 *
 * @deprecated This class does not propagate exceptions consistently and will be removed. Use {@link FutureStreamJoiner}
 * instead.
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListenableFutureCollector implements Collector<ListenableFuture<Void>, Void, ListenableFuture<Void>>
{
    public static ListenableFutureCollector concurrent(int maximumConcurrentFutures)
    {
        if (maximumConcurrentFutures <= 0)
        {
            throw new IllegalArgumentException("Maximum concurrent futures must be greater than 0");
        }
        return new ListenableFutureCollector(maximumConcurrentFutures);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private final class Callback implements FutureCallback<Void>
    {
        @Override
        public void onSuccess(Void value)
        {
            synchronized (semaphore)
            {
                runningFutures.decrementAndGet();
                if (collectionWasFinished())
                {
                    setResultValueIfAllFuturesFinished();
                }
                semaphore.notifyAll();
            }
        }

        @Override
        public void onFailure(Throwable t)
        {
            synchronized (semaphore)
            {
                runningFutures.decrementAndGet();
                if (collectionWasFinished())
                {
                    result.setException(t);
                }
                else
                {
                    throwable = t;
                }
                semaphore.notifyAll();
            }
        }
    }

    private final int maximumConcurrentFutures;
    private final Object semaphore = new Object();

    private final AtomicInteger runningFutures = new AtomicInteger();
    private Throwable throwable;
    private SettableFuture<Void> result;

    @Override
    public Supplier<Void> supplier()
    {
        return () -> null;
    }

    @Override
    public BiConsumer<Void, ListenableFuture<Void>> accumulator()
    {
        return this::accumulateAndWait;
    }

    private void accumulateAndWait(Void a, ListenableFuture<Void> future)
    {
        synchronized (semaphore)
        {
            verifyNoPreviousException();
            consumeFuture(future);

            try
            {
                while (runningFutures.get() >= maximumConcurrentFutures)
                {
                    semaphore.wait();
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread()
                    .interrupt();
                throw new UncheckedInterruptedException(e);
            }
        }
    }

    private void verifyNoPreviousException()
    {
        if (throwable != null)
        {
            throw new IllegalStateException("Other future raised an exception", throwable);
        }
    }

    private void consumeFuture(ListenableFuture<Void> future)
    {
        runningFutures.incrementAndGet();
        Futures.addCallback(future, new Callback(), MoreExecutors.directExecutor());
    }

    @Override
    public BinaryOperator<Void> combiner()
    {
        return (a1, a2) -> null;
    }

    @Override
    public Function<Void, ListenableFuture<Void>> finisher()
    {
        return a -> {
            synchronized (semaphore)
            {
                result = SettableFuture.create();
                setResultValueIfAllFuturesFinished();
                return result;
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics()
    {
        return EnumSet.of(Characteristics.UNORDERED);
    }

    private boolean collectionWasFinished()
    {
        return result != null;
    }

    private void setResultValueIfAllFuturesFinished()
    {
        if (runningFutures.get() == 0)
        {
            result.set(null);
        }
    }
}
