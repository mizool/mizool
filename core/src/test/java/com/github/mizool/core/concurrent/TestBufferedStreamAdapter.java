package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public abstract class TestBufferedStreamAdapter<F extends Future<Object>>
{
    public static final class CompletableFutureMode extends TestBufferedStreamAdapter<CompletableFuture<Object>>
    {
        @Override
        protected Stream<Object> runTest(Stream<CompletableFuture<Object>> stream, int bufferSize)
        {
            return BufferedStreamAdapter.completable()
                .adapt(stream, bufferSize, executorService);
        }

        @Override
        protected FutureSuite<CompletableFuture<Object>> createSuite(int corePoolSize)
        {
            return FutureSuite.completable(corePoolSize);
        }

        @Override
        protected CompletableFuture<Object> runAsFuture(ThrowingStreamHarness.Task runnable)
        {
            return CompletableFuture.supplyAsync(runnable, executorService);
        }
    }

    public static final class ListenableFutureMode extends TestBufferedStreamAdapter<ListenableFuture<Object>>
    {
        protected ListeningExecutorService listeningExecutorService;

        @BeforeMethod
        public void setUp()
        {
            super.setUp();
            listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
        }

        @Override
        protected Stream<Object> runTest(Stream<ListenableFuture<Object>> stream, int bufferSize)
        {
            return BufferedStreamAdapter.listenable()
                .adapt(stream, bufferSize, executorService);
        }

        @Override
        protected FutureSuite<ListenableFuture<Object>> createSuite(int corePoolSize)
        {
            return FutureSuite.listenable(corePoolSize);
        }

        @Override
        protected ListenableFuture<Object> runAsFuture(ThrowingStreamHarness.Task runnable)
        {
            return listeningExecutorService.submit(runnable, null);
        }
    }

    private static final int IMMEDIATE = 0;
    private static final int FAST = 100;
    private static final int SLOW = 1000;

    private FutureSuite<F> suite;
    protected ExecutorService executorService;

    @BeforeMethod
    public void setUp()
    {
        suite = createSuite(100);
        executorService = Executors.newCachedThreadPool();
    }

    protected abstract FutureSuite<F> createSuite(int i);

    protected abstract Stream<Object> runTest(Stream<F> stream, int bufferSize);

    protected abstract F runAsFuture(ThrowingStreamHarness.Task runnable);

    @AfterMethod
    public void tearDown()
    {
        executorService.shutdown();
        suite.tearDown();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLowerTaskLimitBoundary()
    {
        runTest(Stream.of(), 0);
    }

    @Test(dataProvider = "parallelizationVariants", timeOut = FutureSuite.TEST_TIMEOUT)
    public void testParallelization(String name, int bufferSize, long[] durations)
    {
        suite.addItems(durations);

        Stream<Object> actual = runTest(suite.stream(), bufferSize);

        suite.assertContainsExpectedResults(actual);
        suite.assertStartedFutures(durations.length);
        suite.assertFinishedFutures(durations.length);
        suite.assertMaximumConcurrentFutures(Math.min(bufferSize, durations.length));
    }

    @DataProvider
    protected Object[][] parallelizationVariants()
    {
        return new Object[][]{
            { "empty stream", 1, new long[]{} },
            { "single immediate future", 1, new long[]{ IMMEDIATE } },
            { "single delayed future", 1, new long[]{ FAST } },
            { "multiple futures", 5, new long[]{ FAST, FAST, FAST, FAST, FAST } },
            { "throttling", 2, new long[]{ FAST, FAST, FAST, FAST, FAST } }
        };
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT)
    public void testWaitsForStreamDepletion()
    {
        suite.addItems(IMMEDIATE, IMMEDIATE, IMMEDIATE, IMMEDIATE, IMMEDIATE);

        Stream<F> delayedFutures = suite.stream()
            .peek(future -> Threads.sleep(200));

        Stream<Object> actual = runTest(delayedFutures, 2);

        suite.assertContainsExpectedResults(actual);
        suite.assertFinishedFutures(5);
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT)
    public void testAvoidsExcessiveBuffer()
    {
        suite.addItems(FAST, FAST, FAST, FAST);

        int bufferSize = 2;
        int sufficientWaitTime = FAST * 2;

        Stream<Object> actual = runTest(suite.stream(), bufferSize);

        Threads.sleep(sufficientWaitTime);
        suite.assertStartedFutures(bufferSize);

        actual.findFirst();
        Threads.sleep(sufficientWaitTime);
        suite.assertStartedFutures(bufferSize + 1);
    }

    @Test(dataProvider = "timingVariants", timeOut = FutureSuite.TEST_TIMEOUT)
    public void testCompletionTiming(
        String name, int bufferSize, long[] durations, int cutoff, int expectedEarlyResults, int expectedLateResults)
    {
        suite.addItems(durations);

        long start = System.currentTimeMillis();

        Stream<Object> actual = runTest(suite.stream(), bufferSize);

        List<Long> timings = actual.mapToLong(result -> System.currentTimeMillis() - start)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        int earlyResults = 0;
        int lateResults = 0;

        for (Long timing : timings)
        {
            if (timing < cutoff)
            {
                earlyResults++;
            }
            else
            {
                lateResults++;
            }
        }

        suite.assertFinishedFutures(durations.length);

        assertThat(earlyResults).isEqualTo(expectedEarlyResults);
        assertThat(lateResults).isEqualTo(expectedLateResults);
    }

    @DataProvider
    protected Object[][] timingVariants()
    {
        return new Object[][]{
            { "single buffer slow future delays subsequent", 1, new long[]{ FAST, SLOW, FAST }, 900, 1, 2 },
            { "dual buffer slow future is bypassed", 2, new long[]{ FAST, SLOW, FAST }, 900, 2, 1 },
            { "dual buffer slow futures delay subsequent", 2, new long[]{ FAST, SLOW, SLOW, FAST }, 900, 1, 3 },
            { "big buffer slow futures are bypassed", 8, new long[]{ FAST, SLOW, SLOW, FAST }, 900, 2, 2 }
        };
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT,
        dataProvider = "throwablePositions",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testThrowablePosition(ThrowingStreamHarness harness)
    {
        ThrowableAssert.ThrowingCallable throwingCallable = prepareHarnessStreaming(harness);

        harness.assertThrowsException(throwingCallable);
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT,
        dataProvider = "singletonStreamsForEachThrowableType",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testThrowableType(ThrowingStreamHarness harness)
    {
        ThrowableAssert.ThrowingCallable throwingCallable = prepareHarnessStreaming(harness);

        harness.assertThrowsException(throwingCallable);
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT,
        dataProvider = "consumptionFailingStreamsForEachThrowableType",
        dataProviderClass = ThrowingStreamHarness.class)
    public void testFailingStreamConsumption(ThrowingStreamHarness harness)
    {
        ThrowableAssert.ThrowingCallable throwingCallable = prepareHarnessStreaming(harness);

        harness.assertThrowsException(throwingCallable);
    }

    private ThrowableAssert.ThrowingCallable prepareHarnessStreaming(ThrowingStreamHarness harness)
    {
        Stream<F> stream = harness.stream()
            .map(this::runAsFuture);

        return () -> {
            runTest(stream, 1).forEach(ignored -> {
            });
        };
    }
}
