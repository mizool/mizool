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
