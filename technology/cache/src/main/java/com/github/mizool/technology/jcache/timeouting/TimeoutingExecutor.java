package com.github.mizool.technology.jcache.timeouting;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.mizool.core.concurrent.Futures;
import com.github.mizool.core.configuration.Config;

public class TimeoutingExecutor
{
    private static final long CACHE_TIMEOUT = Config.systemProperties()
        .child("cache.timeout")
        .longValue()
        .read()
        .orElse(10000L);

    private final ExecutorService executorService = Executors.newWorkStealingPool();

    public <T> T execute(Callable<T> callable)
    {
        return Futures.get(executorService.submit(callable),
            Duration.of(TimeoutingExecutor.CACHE_TIMEOUT, ChronoUnit.MILLIS));
    }

    public void execute(Runnable runnable)
    {
        Futures.get(executorService.submit(runnable), Duration.of(TimeoutingExecutor.CACHE_TIMEOUT, ChronoUnit.MILLIS));
    }
}
