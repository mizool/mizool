package com.github.mizool.core.concurrent;

import static com.github.mizool.core.concurrent.ListenableFutureCollector.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.ListenableFuture;

public class TestListenableFutureCollector
{
    private static final int IMMEDIATE = 0;
    private static final int FAST = 100;
    private static final int SLOW = 1000;

    private FutureSuite<ListenableFuture<Object>> suite;

    @BeforeMethod
    public void setUp()
    {
        suite = FutureSuite.listenable(100);
    }

    @AfterMethod
    public void tearDown()
    {
        suite.tearDown();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLowerTaskLimitBoundary()
    {
        concurrent(0);
    }

    @Test(dataProvider = "parallelizationVariants", timeOut = FutureSuite.TEST_TIMEOUT)
    public void testParallelization(String name, int maximumConcurrentFutures, long[] durations)
        throws InterruptedException, ExecutionException
    {
        suite.addItems(durations);

        suite.stream()
            .map(ResultVoidingFuture::new)
            .collect(concurrent(maximumConcurrentFutures))
            .get();

        suite.assertStartedFutures(durations.length);
        suite.assertFinishedFutures(durations.length);
        suite.assertMaximumConcurrentFutures(Math.min(maximumConcurrentFutures, durations.length));
    }

    @DataProvider
    private Object[][] parallelizationVariants()
    {
        return new Object[][]{
            { "empty stream", 1, new long[]{} },
            { "single immediate future", 1, new long[]{ IMMEDIATE } },
            { "single delayed future", 1, new long[]{ FAST } },
            { "multiple futures", 5, new long[]{ FAST, FAST, FAST, FAST, FAST } },
            { "multiple throttling", 2, new long[]{ FAST, FAST, FAST, FAST, FAST } }
        };
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT)
    public void testResultIsReadyImmediately() throws ExecutionException, InterruptedException, TimeoutException
    {
        suite.addItems(IMMEDIATE);

        ListenableFuture<Void> result = suite.stream()
            .map(ResultVoidingFuture::new)
            .collect(concurrent(1));

        Thread.sleep(1);

        result.get(0, TimeUnit.MILLISECONDS);

        suite.assertFinishedFutures(1);
        suite.assertMaximumConcurrentFutures(1);
    }

    @Test(timeOut = FutureSuite.TEST_TIMEOUT)
    public void testFastFuturesCompleteEarly() throws ExecutionException, InterruptedException
    {
        suite.addItems(SLOW, FAST, SLOW, FAST, SLOW);

        int sufficientWaitTime = FAST * 2;

        ListenableFuture<Void> result = suite.stream()
            .map(ResultVoidingFuture::new)
            .collect(concurrent(5));

        Thread.sleep(sufficientWaitTime);

        suite.assertStartedFutures(5);
        suite.assertFinishedFutures(2);

        result.get();

        suite.assertFinishedFutures(5);
        suite.assertMaximumConcurrentFutures(5);
    }
}
