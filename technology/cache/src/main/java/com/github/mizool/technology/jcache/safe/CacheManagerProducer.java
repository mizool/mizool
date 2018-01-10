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
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.config.ConfigurableCacheManager;
import com.github.mizool.technology.jcache.timeouting.TimeoutingCacheManager;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CacheManagerProducer
{
    @Produces
    public CacheManager produce(
        CacheWatchdog cacheWatchdog,
        Instance<SafeCacheManager> safeCacheManagers,
        Instance<NoOpCacheManager> noOpCacheManagers,
        Instance<TimeoutingCacheManager> timeoutingCacheManagers,
        Instance<ConfigurableCacheManager> configurableCacheManagers)
    {
        if (cacheWatchdog.isCacheBroken())
        {
            return noOpCacheManagers.get();
        }

        try
        {
            CachingProvider provider = Caching.getCachingProvider();
            CacheManager cacheManager = provider.getCacheManager(provider.getDefaultURI(),
                provider.getDefaultClassLoader());

            TimeoutingCacheManager timeoutingCacheManager = timeoutingCacheManagers.get();
            timeoutingCacheManager.setTarget(cacheManager);

            SafeCacheManager safeCacheManager = safeCacheManagers.get();
            safeCacheManager.setTarget(timeoutingCacheManager);

            ConfigurableCacheManager configurableCacheManager = configurableCacheManagers.get();
            configurableCacheManager.setTarget(safeCacheManager);

            return configurableCacheManager;
        }
        catch (RuntimeException e)
        {
            SafeCacheLogHelper.log("Error obtaining cache manager", e, log);
            cacheWatchdog.cacheOperationFailed();
            return new NoOpCacheManager();
        }
    }
}