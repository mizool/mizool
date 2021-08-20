package com.github.mizool.technology.jcache.safe;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.NonDefault;
import com.github.mizool.technology.jcache.config.ConfigurableCacheManager;
import com.github.mizool.technology.jcache.timeouting.TimeoutingCacheManager;

@Slf4j
class CacheManagerProducer
{
    @Singleton
    @Produces
    public CacheManager produce(
        CacheWatchdog cacheWatchdog,
        @NonDefault SafeCacheManager safeCacheManager,
        @NonDefault NoOpCacheManager noOpCacheManager,
        @NonDefault TimeoutingCacheManager timeoutingCacheManager,
        @NonDefault ConfigurableCacheManager configurableCacheManager)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return noOpCacheManager;
        }

        try
        {
            CachingProvider provider = Caching.getCachingProvider();
            CacheManager cacheManager = provider.getCacheManager(provider.getDefaultURI(),
                provider.getDefaultClassLoader());

            timeoutingCacheManager.setTarget(cacheManager);
            safeCacheManager.setTarget(timeoutingCacheManager);
            configurableCacheManager.setTarget(safeCacheManager);

            return configurableCacheManager;
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.onObtainManager(e, log);
            cacheWatchdog.cacheOperationFailed();
            return new NoOpCacheManager();
        }
    }

    public void dispose(@Disposes CacheManager cacheManager)
    {
        cacheManager.close();
    }
}
