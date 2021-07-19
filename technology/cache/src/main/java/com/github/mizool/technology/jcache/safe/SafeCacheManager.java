package com.github.mizool.technology.jcache.safe;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.inject.Inject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.NonDefault;
import com.github.mizool.technology.jcache.common.AbstractDelegatingCacheManager;

@Slf4j
@NonDefault
class SafeCacheManager extends AbstractDelegatingCacheManager
{
    private final CacheWatchdog cacheWatchdog;

    @Inject
    SafeCacheManager(@NonNull CacheWatchdog cacheWatchdog)
    {
        this.cacheWatchdog = cacheWatchdog;
    }

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
            cacheWatchdog.resetCacheIfRequired(getTarget());
            return new SafeCache<>(super.createCache(cacheName, configuration), cacheWatchdog);
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onCreate(cacheName, e, log);
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
            cacheWatchdog.resetCacheIfRequired(getTarget());
            Cache<K, V> cache = super.getCache(cacheName);
            if (cache != null)
            {
                cache = new SafeCache<>(cache, cacheWatchdog);
            }
            return cache;
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onObtain(cacheName, e, log);
            cacheWatchdog.cacheOperationFailed();
            return new NoOpCache<>();
        }
    }
}
