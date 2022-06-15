package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;

public class TestFutures
{
    @RequiredArgsConstructor
    private static class FailingComputation<T> implements Supplier<T>, Callable<T>
    {
        @NonNull
        protected final Class<? extends Throwable> throwableClass;

        @Override
        public T call()
        {
            return fail();
        }

        @Override
        public T get()
        {
            return fail();
        }

        @SneakyThrows
        public T fail()
        {
            throw Throwables.instantiate(throwableClass, "Simulated failure");
        }

        @Override
        public String toString()
        {
            return "FailingComputation{" + throwableClass.getSimpleName() + "}";
        }
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

    @AfterMethod
    public void tearDown()
    {
        // Clear the thread's interrupted status
        Thread.interrupted();
    }

    @Test
    public void testGetReturnsValue()
    {
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(42);

        Integer result = Futures.get(future);

        Assertions.assertThat(result)
            .isEqualTo(42);
    }

    @Test
    public void testGetThrowsUncheckedExecutionException()
    {
        Future<Void> future = executorService.submit(() -> {
            throw new DummyRuntimeException();
        });

        assertThatThrownBy(() -> Futures.get(future)).isInstanceOf(UncheckedExecutionException.class)
            .hasRootCauseExactlyInstanceOf(DummyRuntimeException.class);
    }

    @Test
    public void testGetThrowsCancellationException()
    {
        Future<?> future = executorService.submit(() -> sleepUnchecked(250));
        future.cancel(true);

        assertThatThrownBy(() -> Futures.get(future)).isInstanceOf(CancellationException.class);
    }

    @Test
    public void testGetThrowsUncheckedInterruptedException() throws Exception
    {
        Future<?> mockedFuture = Mockito.mock(Future.class);
        when(mockedFuture.get()).thenThrow(InterruptedException.class);

        assertThatThrownBy(() -> Futures.get(mockedFuture)).isInstanceOf(UncheckedInterruptedException.class);
    }

    @Test(timeOut = 1000)
    public void testGetThrowsUncheckedTimeoutException()
    {
        Future<?> future = executorService.submit(() -> sleepUnchecked(500));

        Duration timeout = Duration.ofMillis(250);
        assertThatThrownBy(() -> Futures.get(future, timeout)).isInstanceOf(UncheckedTimeoutException.class);
    }

    private void sleepChecked(int milliseconds) throws InterruptedException
    {
        TimeUnit.MILLISECONDS.sleep(milliseconds);
    }

    private void sleepUnchecked(int milliseconds)
    {
        try
        {
            sleepChecked(milliseconds);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread()
                .interrupt();
            throw new UncheckedInterruptedException(e);
        }
    }

    @Test
    public void testToVoidResultWithCompletable() throws Exception
    {
        CompletableFuture<Integer> future = CompletableFuture.completedFuture(42);

        Object result = Futures.toVoidResult(future)
            .get();

        assertThat(result).isNull();
    }

    @Test
    public void testToVoidResultWithListenable() throws Exception
    {
        ListenableFuture<Integer> future = com.google.common.util.concurrent.Futures.immediateFuture(42);

        Object result = Futures.toVoidResult(future)
            .get();

        assertThat(result).isNull();
    }

    @Test(dataProvider = "failingComputationsForEachThrowableType")
    public void testToVoidResultWithCompletableKeepsThrowables(FailingComputation<?> failingComputation)
    {
        CompletableFuture<?> originalFuture = CompletableFuture.supplyAsync(failingComputation, executorService);

        CompletableFuture<Void> voidFuture = Futures.toVoidResult(originalFuture);

        assertThat(causalChainThrownBy(voidFuture)).isEqualTo(causalChainThrownBy(originalFuture));
    }

    @Test(dataProvider = "failingComputationsForEachThrowableType")
    public void testToVoidResultWithListenableKeepsThrowables(FailingComputation<?> failingComputation)
    {
        ListenableFuture<?> originalFuture = listeningExecutorService.submit(failingComputation);

        ListenableFuture<Void> voidFuture = Futures.toVoidResult(originalFuture);

        assertThat(causalChainThrownBy(voidFuture)).isEqualTo(causalChainThrownBy(originalFuture));
    }

    private List<? extends Class<?>> causalChainThrownBy(Future<?> future)
    {
        Throwable throwable = catchThrowable(future::get);
        return getCausalChainClasses(throwable);
    }

    private List<? extends Class<?>> getCausalChainClasses(Throwable throwable)
    {
        return com.google.common.base.Throwables.getCausalChain(throwable)
            .stream()
            .map(Object::getClass)
            .collect(Collectors.toList());
    }

    @DataProvider
    protected static Object[][] failingComputationsForEachThrowableType()
    {
        return new Object[][]{
            { new FailingComputation<>(DummyCheckedException.class) },
            { new FailingComputation<>(DummyRuntimeException.class) },
            { new FailingComputation<>(DummyError.class) },
            { new FailingComputation<>(DummyThrowable.class) }
        };
    }
}
