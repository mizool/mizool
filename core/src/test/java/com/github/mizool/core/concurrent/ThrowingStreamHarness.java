package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.DataProvider;

@Slf4j
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThrowingStreamHarness
{
    /**
     * A task to be run/called by a future.
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Task implements Runnable, Supplier<Object>
    {
        private final int number;
        private final Class<? extends Throwable> throwableClass;

        @Override
        public Object get()
        {
            run();
            return this;
        }

        @SneakyThrows
        @Override
        public void run()
        {
            String me = "Runnable #" + number;

            log.info("{} running.", me);
            if (shouldFail())
            {
                log.info("{} throws {}.", me, throwableClass.getSimpleName());

                throw Throwables.instantiate(throwableClass, me + " failed");
            }
            log.info("{} completed.", me);
        }

        private boolean shouldFail()
        {
            return throwableClass != null;
        }
    }

    private interface TaskStreamFactory
    {
        Stream<Task> createStream(@NonNull Class<? extends Throwable> desiredThrowableClass);

        String getAssertableExceptionMessage();
    }

    @DataProvider
    protected static Object[][] throwablePositions()
    {
        int first = 1;
        int last = 5;

        ArrayList<Object[]> result = new ArrayList<>();
        for (int size = first; size <= last; size++)
        {
            for (int failing = first; failing <= size; failing++)
            {
                result.add(new Object[]{
                    ThrowingStreamHarness.create(size, failing)
                });
            }
        }

        return result.toArray(new Object[][]{});
    }

    private static ThrowingStreamHarness create(int count, int failingRunnableNumber)
    {
        return builder().taskStreamFactory(streamOfSometimesFailingTasks(count, failingRunnableNumber))
            .desiredThrowableClass(RuntimeException.class)
            .label("'" + count + " runnables - #" + failingRunnableNumber + " fails'")
            .build();
    }

    @DataProvider
    protected static Object[][] singletonStreamsForEachThrowableType()
    {
        return new Object[][]{
            { createSingletonStream(DummyCheckedException.class) },
            { createSingletonStream(DummyRuntimeException.class) },
            { createSingletonStream(DummyError.class) },
            { createSingletonStream(DummyThrowable.class) }
        };
    }

    private static ThrowingStreamHarness createSingletonStream(Class<? extends Throwable> desiredThrowableClass)
    {
        return builder().taskStreamFactory(streamOfSometimesFailingTasks(1, 1))
            .desiredThrowableClass(desiredThrowableClass)
            .label(desiredThrowableClass.getSimpleName())
            .build();
    }

    private static TaskStreamFactory streamOfSometimesFailingTasks(int count, int failingRunnableNumber)
    {
        if (count < 1)
        {
            throw new IllegalArgumentException("count must be greater than 0");
        }
        if (failingRunnableNumber < 1 || failingRunnableNumber > count)
        {
            throw new IllegalArgumentException("failingRunnable must be between 1 and count");
        }

        TaskStreamFactory result = new TaskStreamFactory()
        {
            @Override
            public Stream<Task> createStream(Class<? extends Throwable> desiredThrowableClass)
            {
                return IntStream.rangeClosed(1, count)
                    .mapToObj(number -> {
                        Task task = new Task(number, number == failingRunnableNumber ? desiredThrowableClass : null);
                        return task;
                    });
            }

            @Override
            public String getAssertableExceptionMessage()
            {
                return "Runnable #" + failingRunnableNumber + " failed";
            }
        };
        return result;
    }

    @DataProvider
    protected static Object[][] consumptionFailingStreamsForEachThrowableType()
    {
        return new Object[][]{
            { createSingletonStream(DummyCheckedException.class) },
            { createSingletonStream(DummyRuntimeException.class) },
            { createSingletonStream(DummyError.class) },
            { createSingletonStream(DummyThrowable.class) }
        };
    }

    @NonNull
    private final TaskStreamFactory taskStreamFactory;

    @NonNull
    private final Class<? extends Throwable> desiredThrowableClass;

    @NonNull
    private final String label;

    @Override
    public String toString()
    {
        return label;
    }

    public Stream<Task> stream()
    {
        return taskStreamFactory.createStream(desiredThrowableClass);
    }

    public void assertThrowsException(ThrowableAssert.ThrowingCallable throwingCallable)
    {
        Throwable result = catchThrowable(throwingCallable);

        assertThat(result).isInstanceOf(desiredThrowableClass)
            .hasNoCause()
            .hasMessage(taskStreamFactory.getAssertableExceptionMessage());
    }

    public void assertThrowsWrappedException(
        ThrowableAssert.ThrowingCallable throwingCallable, Class<? extends Exception> wrapperExceptionClass)
    {
        Throwable result = catchThrowable(throwingCallable);

        assertThat(result).describedAs("wrapper")
            .isExactlyInstanceOf(wrapperExceptionClass);

        assertThat(result.getCause()).describedAs("cause")
            .isExactlyInstanceOf(desiredThrowableClass)
            .hasNoCause()
            .hasMessage(taskStreamFactory.getAssertableExceptionMessage());
    }
}
