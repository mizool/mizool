/*
 * Copyright 2017-2020 incub8 Software Labs GmbH
 * Copyright 2017-2020 protel Hotelsoftware GmbH
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
