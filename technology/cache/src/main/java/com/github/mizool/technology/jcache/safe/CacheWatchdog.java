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

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CacheWatchdog
{
    private static final String CACHE_RETRY_PERIOD_PROPERTY_NAME = "cache.retryPeriod";
    private static final long CACHE_RETRY_PERIOD = Long.parseLong(System.getProperty(CACHE_RETRY_PERIOD_PROPERTY_NAME,
        Long.toString(300000)));

    private final CacheWatchdogTimestamp cacheWatchdogTimestamp;

    public boolean isCacheBroken()
    {
        return System.currentTimeMillis() - CACHE_RETRY_PERIOD <= cacheWatchdogTimestamp.getTimestamp();
    }

    public void cacheOperationFailed()
    {
        cacheWatchdogTimestamp.setTimestamp(System.currentTimeMillis());
    }
}