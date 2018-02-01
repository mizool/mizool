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

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
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
}