package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;

public class TestFutures
{
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

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
}
