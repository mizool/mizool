/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.CacheMethodsUsedByReferenceImplementation;

@Slf4j
@RequiredArgsConstructor
class SafeCache<K, V> implements Cache<K, V>
{
    @Delegate(excludes = { CacheMethodsUsedByReferenceImplementation.class })
    @NonNull
    private final Cache<K, V> target;

    @NonNull
    private final CacheWatchdog cacheWatchdog;

    @Override
    public V get(K key)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return null;
        }

        try
        {
            cacheWatchdog.resetCacheIfRequired(target.getCacheManager());
            return target.get(key);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onGet(target.getName(), key.toString(), e, log);
            cacheWatchdog.cacheOperationFailed();
            return null;
        }
    }

    @Override
    public void put(K key, V value)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return;
        }

        try
        {
            cacheWatchdog.resetCacheIfRequired(target.getCacheManager());
            target.put(key, value);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onPut(target.getName(), key.toString(), e, log);
            cacheWatchdog.cacheOperationFailed();
        }
    }

    @Override
    public boolean remove(K key)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return false;
        }

        try
        {
            cacheWatchdog.resetCacheIfRequired(target.getCacheManager());
            return target.remove(key);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onRemove(target.getName(), key.toString(), e, log);
            cacheWatchdog.cacheOperationFailed();
            return false;
        }
    }

    @Override
    public void removeAll()
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return;
        }

        try
        {
            cacheWatchdog.resetCacheIfRequired(target.getCacheManager());
            target.removeAll();
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onRemoveAll(target.getName(), e, log);
            cacheWatchdog.cacheOperationFailed();
        }
    }
}