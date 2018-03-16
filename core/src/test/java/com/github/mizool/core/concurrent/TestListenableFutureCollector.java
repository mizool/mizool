package com.github.mizool.core.concurrent;

import static com.github.mizool.core.concurrent.ListenableFutureCollector.concurrent;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class TestListenableFutureCollector
{
    private AtomicInteger maximumConcurrent;
    private AtomicInteger running;
    private AtomicInteger totalFinished;
    private ScheduledExecutorService executorService;

    @BeforeMethod
    public void setUp()
    {
        maximumConcurrent = new AtomicInteger();
        running = new AtomicInteger();
        totalFinished = new AtomicInteger();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @AfterMethod
    public void tearDown()
    {
        executorService.shutdown();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLowerTaskLimitBoundary()
    {
        concurrent(0);
    }

    @Test(timeOut = 5000)
    public void testEmptyStream() throws ExecutionException, InterruptedException
    {
        IntStream.empty().mapToObj(scheduledFuture()).collect(concurrent(1)).get();
    }

    @Test(timeOut = 5000)
    public void testSingleFuture() throws ExecutionException, InterruptedException
    {
        IntStream.of(0).mapToObj(scheduledFuture()).collect(concurrent(1)).get();

        assertFinishedFutures(1);
        assertMaximumConcurrentFutures(1);
    }

    @Test(timeOut = 5000)
    public void testResultIsReadyImmediately() throws ExecutionException, InterruptedException, TimeoutException
    {
        ListenableFuture<Void> result = IntStream.of(0).mapToObj(scheduledFuture()).collect(concurrent(1));

        Thread.sleep(1);

        result.get(0, TimeUnit.MILLISECONDS);

        assertFinishedFutures(1);
        assertMaximumConcurrentFutures(1);
    }

    @Test(timeOut = 5000)
    public void testMultipleFutures() throws ExecutionException, InterruptedException
    {
        IntStream.of(0, 0, 0, 0, 0).mapToObj(scheduledFuture()).collect(concurrent(7)).get();
        assertFinishedFutures(5);
        assertMaximumConcurrentFutures(5);
    }

    @Test(timeOut = 5000)
    public void testThrottling() throws ExecutionException, InterruptedException
    {
        IntStream.of(500, 500, 500, 500, 500).mapToObj(scheduledFuture()).collect(concurrent(2)).get();
        assertFinishedFutures(5);
        assertMaximumConcurrentFutures(2);
    }

    @Test(timeOut = 5000)
    public void testFastFuturesCompleteEarly() throws ExecutionException, InterruptedException
    {
        ListenableFuture<Void> result = IntStream.of(500, 50, 500, 50, 500)
            .mapToObj(scheduledFuture())
            .collect(concurrent(5));

        Thread.sleep(100);

        assertFinishedFutures(2);

        result.get();

        assertFinishedFutures(5);
        assertMaximumConcurrentFutures(5);
    }

    private IntFunction<ListenableFuture<Void>> scheduledFuture()
    {
        return delay -> {
            int nowRunning = running.incrementAndGet();
            maximumConcurrent.accumulateAndGet(nowRunning, Math::max);
            SettableFuture<Void> future = SettableFuture.create();
            executorService.schedule(() -> {
                running.decrementAndGet();
                totalFinished.incrementAndGet();
                future.set(null);
            }, delay, TimeUnit.MILLISECONDS);
            return future;
        };
    }

    private void assertFinishedFutures(int expected)
    {
        assertThat(totalFinished.get()).isEqualTo(expected);
    }

    private void assertMaximumConcurrentFutures(int expected)
    {
        assertThat(maximumConcurrent.get()).isEqualTo(expected);
    }
}