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

import lombok.experimental.UtilityClass;

import org.slf4j.Logger;

import com.github.mizool.technology.jcache.timeouting.CacheTimeoutException;

@UtilityClass
class SafeCacheLogHelper
{
    public void onObtainManager(Exception e, Logger log)
    {
        log("cacheManager obtain failed", e, log);
    }

    public void onCreate(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache create failed", e, log);
    }

    public void onObtain(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache obtain failed", e, log);
    }

    public void onGet(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache get failed: " + key, e, log);
    }

    public void onPut(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache put failed: " + key, e, log);
    }

    public void onRemove(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache remove failed: " + key, e, log);
    }

    public void onRemoveAll(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache remove all failed", e, log);
    }

    private void log(String message, Exception e, Logger log)
    {
        if (e instanceof CacheTimeoutException)
        {
            log.warn("{} - {}", message, e.getClass().getName());
            log.debug(message, e);
        }
        else
        {
            log.warn("{} - {}", message, e);
        }
    }
}