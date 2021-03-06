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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Singleton;

import lombok.Data;

@Singleton
@Data
class CacheWatchdogState
{
    private final AtomicLong timestamp = new AtomicLong(0);
    private final AtomicBoolean cacheResetRequired = new AtomicBoolean(false);

    public void setTimestamp(long timestamp)
    {
        this.timestamp.set(timestamp);
    }

    public long getTimestamp()
    {
        return timestamp.get();
    }

    public boolean toggleCacheResetToRequired()
    {
        return this.cacheResetRequired.compareAndSet(false, true);
    }

    public boolean toggleCacheResetToNotRequired()
    {
        return this.cacheResetRequired.compareAndSet(true, false);
    }
}