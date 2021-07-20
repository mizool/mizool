package com.github.mizool.core.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Slf4j
public abstract class TestFutureStreamJoiner<O extends Future<Object>, V extends Future<Void>>
{
    public static final class CompletableFutureMode
        extends TestFutureStreamJoiner<CompletableFuture<Object>, CompletableFuture<Void>>
    {
        @Override
        public ConcurrentTests.Suite<CompletableFuture<Object>> createSuite(int corePoolSize)
        {
            return ConcurrentTests.completableFutureSuite(corePoolSize);
        }

        @Override
        protected CompletableFuture<Void> toVoidResult(CompletableFuture<Object> future)
        {
            return Futures.toVoidResult(future);
        }

        @Override
        protected CompletableFuture<Void> join(Stream<CompletableFuture<Void>> input, int maximumConcurrentFutures)
        {
            return FutureStreamJoiner.completable()
                .join(input, maximumConcurrentFutures, executorService);
        }

        @Override
        protected CompletableFuture<Void> runAsFuture(ThrowingStreamHarness.Task runnable)
        {
            return CompletableFuture.runAsync(runnable, executorService);
        }
    }

    public static final class ListenableFutureMode
        extends TestFutureStreamJoiner<ListenableFuture<Object>, ListenableFuture<Void>>
    {
        protected ListeningExecutorService listeningExecutorService;

        @BeforeMethod
        public void setUp()
        {
            super.setUp();
            listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
        }

        @Override
        public ConcurrentTests.Suite<ListenableFuture<Object>> createSuite(int corePoolSize)
        {
            return ConcurrentTests.listenableFutureSuite(corePoolSize);
        }

        @Override
        protected ListenableFuture<Void> toVoidResult(ListenableFuture<Object> future)
        {
            return Futures.toVoidResult(future);
        }

        @Override
        protected ListenableFuture<Void> join(Stream<ListenableFuture<Void>> input, int maximumConcurrentFutures)
        {
            return FutureStreamJoiner.listenable()
                .join(input, maximumConcurrentFutures, executorService);
        }

        @Override
        protected ListenableFuture<Void> runAsFuture(ThrowingStreamHarness.Task runnable)
        {
            return listeningExecutorService.submit(runnable, null);
        }
    }

    private static final int IMMEDIATE = 0;
    private static final int FAST = 100;

    private static final int SLOW = 1000;
    private ConcurrentTests.Suite<O> suite;

    protected ExecutorService executorService;

    @BeforeMethod
    public void setUp()
    {
        suite = createSuite(100);
        executorService = Executors.newCachedThreadPool();
    }

    protected abstract ConcurrentTests.Suite<O> createSuite(int corePoolSize);

    @AfterMethod
    public void tearDown()
    {
        suite.tearDown();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLowerTaskLimitBoundary()
    {
        join(Stream.empty(), 0);
    }

    @Test(dataProvider = "parallelizationVariants", timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testParallelization(String name, int maximumConcurrentFutures, long[] durations)
        throws InterruptedException, ExecutionException
    {
        suite.addItems(durations);

        Future<?> result = joinSuite(maximumConcurrentFutures);
        result.get();

        suite.assertStartedFutures(durations.length);
        suite.assertFinishedFutures(durations.length);
        suite.assertMaximumConcurrentFutures(Math.min(maximumConcurrentFutures, durations.length));
    }

    private Future<?> joinSuite(int maximumConcurrentFutures)
    {
        Stream<V> input = suite.stream()
            .map(this::toVoidResult);
        return join(input, maximumConcurrentFutures);
    }

    protected abstract V join(Stream<V> input, int maximumConcurrentFutures);

    protected abstract V toVoidResult(O future);

    protected abstract V runAsFuture(ThrowingStreamHarness.Task runnable);

    @DataProvider
    protected Object[][] parallelizationVariants()
    {
        return new Object[][]{
            { "empty stream", 1, new long[]{} },
            { "single immediate future", 1, new long[]{ IMMEDIATE } },
            { "single delayed future", 1, new long[]{ FAST } },
            { "multiple futures", 5, new long[]{ FAST, FAST, FAST, FAST, FAST } },
            { "multiple throttling", 2, new long[]{ FAST, FAST, FAST, FAST, FAST } }
        };
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testResultIsReadyImmediately() throws ExecutionException, InterruptedException, TimeoutException
    {
        suite.addItems(IMMEDIATE);

        Future<?> result = joinSuite(1);

        Thread.sleep(10);

        result.get(0, TimeUnit.MILLISECONDS);

        suite.assertFinishedFutures(1);
        suite.assertMaximumConcurrentFutures(1);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testFastFuturesCompleteEarly() throws ExecutionException, InterruptedException
    {
        suite.addItems(SLOW, FAST, SLOW, FAST, SLOW);

        int sufficientWaitTime = FAST * 2;

        Future<?> result = joinSuite(5);

        Thread.sleep(sufficientWaitTime);

        suite.assertStartedFutures(5);
        suite.assertFinishedFutures(2);

        result.get();

        suite.assertFinishedFutures(5);
        suite.assertMaximumConcurrentFutures(5);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT,
        dataProvider = "throwablePositions",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testThrowablePosition(ThrowingStreamHarness harness)
    {
        Stream<V> futures = harness.stream()
            .map(this::runAsFuture);
        Future<?> future = join(futures, 1);

        harness.assertThrowsWrappedException(future::get, ExecutionException.class);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT,
        dataProvider = "singletonStreamsForEachThrowableType",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testThrowableType(ThrowingStreamHarness harness)
    {
        Stream<V> futures = harness.stream()
            .map(this::runAsFuture);
        Future<?> future = join(futures, 1);

        harness.assertThrowsWrappedException(future::get, ExecutionException.class);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT,
        dataProvider = "consumptionFailingStreamsForEachThrowableType",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testFailingStreamConsumption(ThrowingStreamHarness harness)
    {
        Stream<V> futures = harness.stream()
            .map(this::runAsFuture);
        Future<?> future = join(futures, 1);

        harness.assertThrowsWrappedException(future::get, ExecutionException.class);
    }
}
