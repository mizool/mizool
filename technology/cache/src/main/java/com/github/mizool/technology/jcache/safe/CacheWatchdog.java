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
package com.github.mizool.technology.jcache.safe;

import javax.cache.CacheManager;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.configuration.Config;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CacheWatchdog
{
    private static final long CACHE_RETRY_PERIOD = Config.systemProperties()
        .child("cache.retryPeriod")
        .longValue()
        .read()
        .orElse(30000L);

    private final CacheWatchdogState cacheWatchdogState;

    public boolean isCacheBroken()
    {
        return System.currentTimeMillis() - CACHE_RETRY_PERIOD <= cacheWatchdogState.getTimestamp();
    }

    public void resetCacheIfRequired(CacheManager cacheManager)
    {
        if (!isCacheBroken() && cacheWatchdogState.toggleCacheResetToNotRequired())
        {
            log.info("Caching coming back online, resetting all caches.");
            try
            {
                for (String cacheName : cacheManager.getCacheNames())
                {
                    cacheManager.getCache(cacheName)
                        .removeAll();
                }
                log.info("All caches reset.");
            }
            catch (RuntimeException e)
            {
                log.warn("Error during cache reset: {}", e.getMessage());
                log.debug("Stack trace for warning", e);
            }
        }
    }

    public void cacheOperationFailed()
    {
        cacheWatchdogState.setTimestamp(System.currentTimeMillis());
        cacheWatchdogState.toggleCacheResetToRequired();
    }
}
