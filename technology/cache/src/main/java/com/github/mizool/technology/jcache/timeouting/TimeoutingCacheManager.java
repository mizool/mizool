/**
 * Copyright 2017 incub8 Software Labs GmbH
 * Copyright 2017 protel Hotelsoftware GmbH
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

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.enterprise.inject.Vetoed;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.CacheManagerMethodsUsedByReferenceImplementation;

/**
 * This class is {@link Vetoed} as it is not intended to be a CDI bean and/or to be injected on its own. It's rather a
 * decorator used in a specific way inside a producer.
 */
@Slf4j
@RequiredArgsConstructor
@Vetoed
public class TimeoutingCacheManager implements CacheManager
{
    @Delegate(excludes = { CacheManagerMethodsUsedByReferenceImplementation.class })
    @NonNull
    private final CacheManager target;

    @NonNull
    private final TimeoutingExecutor timeoutingExecutor;

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
        throws IllegalArgumentException
    {
        Callable<Cache<K, V>> cacheCallable = () -> target.createCache(cacheName, configuration);
        return getTimeoutingCache(cacheCallable);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName)
    {
        Callable<Cache<K, V>> cacheCallable = () -> target.getCache(cacheName);
        return getTimeoutingCache(cacheCallable);
    }

    private <K, V> Cache<K, V> getTimeoutingCache(Callable<Cache<K, V>> cacheCallable)
    {
        Cache<K, V> result = null;

        Cache<K, V> cache = timeoutingExecutor.execute(cacheCallable);
        if (cache != null)
        {
            result = new TimeoutingCache<>(cache, timeoutingExecutor);
        }

        return result;
    }
}