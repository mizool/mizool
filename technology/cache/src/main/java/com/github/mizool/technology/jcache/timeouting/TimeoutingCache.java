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
package com.github.mizool.technology.jcache.timeouting;

import java.util.concurrent.Callable;

import javax.cache.Cache;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.AbstractDelegatingCache;

@Slf4j
class TimeoutingCache<K, V> extends AbstractDelegatingCache<K, V>
{
    private final TimeoutingExecutor timeoutingExecutor;

    public TimeoutingCache(@NonNull Cache<K, V> target, @NonNull TimeoutingExecutor timeoutingExecutor)
    {
        super(target);
        this.timeoutingExecutor = timeoutingExecutor;
    }

    @Override
    public V get(K key)
    {
        Callable<V> callable = () -> getTarget().get(key);
        V result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void put(K key, V value)
    {
        Runnable runnable = () -> getTarget().put(key, value);
        timeoutingExecutor.execute(runnable);
    }

    @Override
    public boolean remove(K key)
    {
        Callable<Boolean> callable = () -> getTarget().remove(key);
        Boolean result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void removeAll()
    {
        Runnable runnable = getTarget()::removeAll;
        timeoutingExecutor.execute(runnable);
    }
}