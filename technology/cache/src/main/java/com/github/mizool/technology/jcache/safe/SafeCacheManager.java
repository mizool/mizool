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
package com.github.mizool.technology.jcache.safe;

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
class SafeCacheManager implements CacheManager
{
    @Delegate(excludes = { CacheManagerMethodsUsedByReferenceImplementation.class })
    @NonNull
    private final CacheManager target;

    @NonNull
    private final CacheWatchdog cacheWatchdog;

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
        throws IllegalArgumentException
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return new NoOpCache<>();
        }

        try
        {
            return new SafeCache<>(target.createCache(cacheName, configuration), cacheWatchdog);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.log("Error creating cache", e, log);
            cacheWatchdog.cacheOperationFailed();
            return new NoOpCache<>();
        }
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return new NoOpCache<>();
        }

        try
        {
            Cache<K, V> cache = target.getCache(cacheName);
            if (cache != null)
            {
                cache = new SafeCache<>(cache, cacheWatchdog);
            }
            return cache;
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.log("Error obtaining cache", e, log);
            cacheWatchdog.cacheOperationFailed();
            return new NoOpCache<>();
        }
    }
}