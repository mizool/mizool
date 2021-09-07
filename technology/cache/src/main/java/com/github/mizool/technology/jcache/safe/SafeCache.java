package com.github.mizool.technology.jcache.safe;

import javax.cache.Cache;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.AbstractDelegatingCache;

@Slf4j
class SafeCache<K, V> extends AbstractDelegatingCache<K, V>
{
    private final CacheWatchdog cacheWatchdog;

    public SafeCache(@NonNull Cache<K, V> target, @NonNull CacheWatchdog cacheWatchdog)
    {
        super(target);
        this.cacheWatchdog = cacheWatchdog;
    }

    @Override
    public V get(K key)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return null;
        }

        try
        {
            cacheWatchdog.resetCacheIfRequired(getTarget().getCacheManager());
            return getTarget().get(key);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onGet(getTarget().getName(), key.toString(), e, log);
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
            cacheWatchdog.resetCacheIfRequired(getTarget().getCacheManager());
            getTarget().put(key, value);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onPut(getTarget().getName(), key.toString(), e, log);
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
            cacheWatchdog.resetCacheIfRequired(getTarget().getCacheManager());
            return getTarget().remove(key);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onRemove(getTarget().getName(), key.toString(), e, log);
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
            cacheWatchdog.resetCacheIfRequired(getTarget().getCacheManager());
            getTarget().removeAll();
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onRemoveAll(getTarget().getName(), e, log);
            cacheWatchdog.cacheOperationFailed();
        }
    }
}
