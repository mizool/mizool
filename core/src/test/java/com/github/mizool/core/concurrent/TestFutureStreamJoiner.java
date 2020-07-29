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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.CodeInconsistencyException;
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
            return FutureStreamJoiner.completable().join(input, maximumConcurrentFutures, executorService);
        }

        @Override
        protected CompletableFuture<Void> runAsFuture(TestRunnable runnable)
        {
            return CompletableFuture.runAsync(runnable, executorService);
        }
    }

    public static final class ListenableFutureMode
        extends TestFutureStreamJoiner<ListenableFuture<Object>, ListenableFuture<Void>>
    {
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
            return FutureStreamJoiner.listenable().join(input, maximumConcurrentFutures, executorService);
        }

        @Override
        protected ListenableFuture<Void> runAsFuture(TestRunnable runnable)
        {
            return listeningExecutorService.submit(runnable, null);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    private static final class DummyError extends Error
    {
        String message;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    private static final class DummyThrowable extends Throwable
    {
        String message;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    private static final class DummyCheckedException extends Exception
    {
        String message;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    private static final class DummyRuntimeException extends RuntimeException
    {
        String message;
    }

    private static final int IMMEDIATE = 0;
    private static final int FAST = 100;
    private static final int SLOW = 1000;

    private ConcurrentTests.Suite<O> suite;
    protected ExecutorService executorService;
    protected ListeningExecutorService listeningExecutorService;

    @BeforeMethod
    public void setUp()
    {
        suite = createSuite(100);
        executorService = Executors.newCachedThreadPool();
        listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
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

    private Future<?> joinSuite(int maximumConcurrentFutures) throws InterruptedException, ExecutionException
    {
        Stream<V> input = suite.stream().map(this::toVoidResult);
        Future<?> future = join(input, maximumConcurrentFutures);
        return future;
    }

    protected abstract V join(Stream<V> input, int maximumConcurrentFutures);

    protected abstract V toVoidResult(O future);

    protected abstract V runAsFuture(TestRunnable runnable);

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

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT, dataProvider = "exceptionHandlingRuns")
    public void testFailingFutures(String name, int startInclusive, int endInclusive, int failingRunnableNumber)
    {
        Stream<V> futures = getTestRunnableStream(startInclusive,
            endInclusive,
            failingRunnableNumber,
            RuntimeException.class).map(this::runAsFuture);

        Future<?> future = join(futures, 1);

        invokeAndAssert(future, failingRunnableNumber, RuntimeException.class);
    }

    @Test(timeOut = ConcurrentTests.TEST_TIMEOUT, dataProvider = "causeExceptionClasses")
    public void testThrowables(String name, Class<IOException> desiredThrowableClass)
    {
        Stream<V> futures = getSingletonRunnableStream(desiredThrowableClass).map(this::runAsFuture);
        Future<?> future = join(futures, 1);
        invokeAndAssert(future, 1, desiredThrowableClass);
    }

    private Stream<TestRunnable> getSingletonRunnableStream(Class<? extends Throwable> desiredThrowableClass)
    {
        return getTestRunnableStream(1, 1, 1, desiredThrowableClass);
    }

    private Stream<TestRunnable> getTestRunnableStream(
        int startInclusive,
        int endInclusive,
        int failingRunnableNumber,
        Class<? extends Throwable> desiredThrowableClass)
    {
        return IntStream.rangeClosed(startInclusive, endInclusive)
            .mapToObj(number -> new TestRunnable(number,
                number == failingRunnableNumber ? desiredThrowableClass : null));
    }

    private void invokeAndAssert(
        Future<?> future, int failingRunnableNumber, Class<? extends Throwable> causeExceptionClass)
    {
        Throwable result = catchThrowable(future::get);

        assertThat(result).describedAs("wrapper").isExactlyInstanceOf(ExecutionException.class);

        assertThat(result.getCause()).describedAs("cause")
            .isInstanceOf(causeExceptionClass)
            .hasNoCause()
            .hasMessage("Runnable #" + failingRunnableNumber + " failed");
    }

    @DataProvider
    protected Object[][] causeExceptionClasses()
    {
        return new Object[][]{
            { "checked exception", DummyCheckedException.class },
            { "runtime exception", DummyRuntimeException.class },
            { "error", DummyError.class },
            { "throwable", DummyThrowable.class }
        };
    }

    @DataProvider
    protected Object[][] exceptionHandlingRuns()
    {
        int first = 1;
        int last = 5;

        ArrayList<Object[]> result = new ArrayList<>();
        for (int size = first; size <= last; size++)
        {
            for (int failing = first; failing <= size; failing++)
            {
                result.add(new Object[]{
                    "'" + size + " runnables - #" + failing + " fails'", first, size, failing
                });
            }
        }

        return result.toArray(new Object[][]{});
    }

    @Value
    private static final class TestRunnable implements Runnable
    {
        int number;
        Class<? extends Throwable> throwableClass;

        @SneakyThrows
        @Override
        public void run()
        {
            String me = "Runnable #" + number;

            log.info("{} running.", me);
            if (shouldFail())
            {
                log.info("{} throws Exception.", me);

                throw instantiateThrowable(me + " failed");
            }
            log.info("{} completed.", me);
        }

        private boolean shouldFail()
        {
            return throwableClass != null;
        }

        private Throwable instantiateThrowable(String message)
        {
            try
            {
                return getStringArgConstructor(throwableClass).newInstance(message);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                throw new CodeInconsistencyException("Could not instantiate " + throwableClass.getCanonicalName(), e);
            }
        }

        private <T> Constructor<T> getStringArgConstructor(Class<T> theClass)
        {
            try
            {
                return theClass.getConstructor(String.class);
            }
            catch (NoSuchMethodException e)
            {
                throw new CodeInconsistencyException("Could not find String arg constructor for " +
                    theClass.getCanonicalName(), e);
            }
        }
    }
}
