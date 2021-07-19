package com.github.mizool.technology.jcache.timeouting;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.UncheckedExecutionException;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class TimeoutingExecutor
{
    private static final long CACHE_TIMEOUT = Config.systemProperties()
        .child("cache.timeout")
        .longValue()
        .read()
        .orElse(10000L);

    private final ExecutorService executorService;

    public <T> T execute(Callable<T> callable)
    {
        try
        {
            Future<T> future = executorService.submit(callable);
            return future.get(TimeoutingExecutor.CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            throw new CacheTimeoutException(e);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread()
                .interrupt();
            throw new UncheckedInterruptedException(e);
        }
        catch (ExecutionException e)
        {
            throw new UncheckedExecutionException(e);
        }
    }

    public void execute(Runnable runnable)
    {
        try
        {
            executorService.submit(runnable)
                .get(TimeoutingExecutor.CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            throw new CacheTimeoutException(e);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread()
                .interrupt();
            throw new UncheckedInterruptedException(e);
        }
        catch (ExecutionException e)
        {
            throw new UncheckedExecutionException(e);
        }
    }
}
