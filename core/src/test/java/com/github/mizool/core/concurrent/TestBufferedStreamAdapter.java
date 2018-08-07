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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.StoreLayerException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class TestBufferedStreamAdapter
{
    private static final int IMMEDIATE = 0;
    private static final int FAST = 100;
    private static final int SLOW = 1000;

    private ConcurrentTests.Suite suite;
    private ExecutorService executorService;

    @BeforeMethod
    public void setUp()
    {
        suite = ConcurrentTests.suite(100);
        executorService = Executors.newCachedThreadPool();
    }

    @AfterMethod
    public void tearDown()
    {
        executorService.shutdown();
        suite.tearDown();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLowerTaskLimitBoundary()
    {
        BufferedStreamAdapter.adapt(null, 0, null);
    }

    @Test(dataProvider = "parallelizationVariants", timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testParallelization(String name, int bufferSize, long[] durations)
    {
        suite.addItems(durations);

        Stream<Object> actual = BufferedStreamAdapter.adapt(suite.stream(), bufferSize, executorService);

        suite.assertContainsExpectedResults(actual);
        suite.assertStartedFutures(durations.length);
        suite.assertFinishedFutures(durations.length);
        suite.assertMaximumConcurrentFutures(Math.min(bufferSize, durations.length));
    }

    @DataProvider
    private Object[][] parallelizationVariants()
    {
        return new Object[][]{
            { "empty stream", 1, new long[]{} },
            { "single immediate future", 1, new long[]{ IMMEDIATE } },
            { "single delayed future", 1, new long[]{ FAST } },
            { "multiple futures", 5, new long[]{ FAST, FAST, FAST, FAST, FAST } },
            { "throttling", 2, new long[]{ FAST, FAST, FAST, FAST, FAST } }
        };
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testWaitsForStreamDepletion()
    {
        suite.addItems(IMMEDIATE, IMMEDIATE, IMMEDIATE, IMMEDIATE, IMMEDIATE);

        Stream<ListenableFuture<Object>> delayedFutures = suite.stream().peek(future -> Threads.sleep(200));

        Stream<Object> actual = BufferedStreamAdapter.adapt(delayedFutures, 2, executorService);

        suite.assertContainsExpectedResults(actual);
        suite.assertFinishedFutures(5);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testAvoidsExcessiveBuffer()
    {
        suite.addItems(FAST, FAST, FAST, FAST);

        int bufferSize = 2;
        int sufficientWaitTime = FAST * 2;

        Stream<Object> actual = BufferedStreamAdapter.adapt(suite.stream(), bufferSize, executorService);

        Threads.sleep(sufficientWaitTime);
        suite.assertStartedFutures(bufferSize);

        actual.findFirst();
        Threads.sleep(sufficientWaitTime);
        suite.assertStartedFutures(bufferSize + 1);
    }

    @Test(dataProvider = "timingVariants", timeOut = ConcurrentTests.TEST_TIMEOUT)
    public void testCompletionTiming(
        String name, int bufferSize, long[] durations, int cutoff, int expectedEarlyResults, int expectedLateResults)
    {
        suite.addItems(durations);

        long start = System.currentTimeMillis();

        Stream<Object> actual = BufferedStreamAdapter.adapt(suite.stream(), bufferSize, executorService);

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
    private Object[][] timingVariants()
    {
        return new Object[][]{
            { "single buffer slow future delays subsequent", 1, new long[]{ FAST, SLOW, FAST }, 900, 1, 2 },
            { "dual buffer slow future is bypassed", 2, new long[]{ FAST, SLOW, FAST }, 900, 2, 1 },
            { "dual buffer slow futures delay subsequent", 2, new long[]{ FAST, SLOW, SLOW, FAST }, 900, 1, 3 },
            { "big buffer slow futures are bypassed", 8, new long[]{ FAST, SLOW, SLOW, FAST }, 900, 2, 2 }
        };
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT, expectedExceptions = UncheckedExecutionException.class)
    public void testTransportsExceptionInStream()
    {
        Stream<ListenableFuture<Void>> stream = Stream.generate(() -> {
            throw new StoreLayerException("some test exception");
        });
        BufferedStreamAdapter.adapt(stream, 2, executorService).forEach(ignored -> {
        });
    }
}