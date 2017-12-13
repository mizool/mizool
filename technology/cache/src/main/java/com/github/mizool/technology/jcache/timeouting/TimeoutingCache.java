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
package com.github.mizool.technology.jcache.timeouting;

import java.util.concurrent.Callable;

import javax.cache.Cache;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.CacheMethodsUsedByReferenceImplementation;

@Slf4j
@RequiredArgsConstructor
class TimeoutingCache<K, V> implements Cache<K, V>
{
    @Delegate(excludes = { CacheMethodsUsedByReferenceImplementation.class })
    @NonNull
    private final Cache<K, V> target;

    @NonNull
    private final TimeoutingExecutor timeoutingExecutor;

    @Override
    public V get(K key)
    {
        Callable<V> callable = () -> target.get(key);
        V result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void put(K key, V value)
    {
        Runnable runnable = () -> target.put(key, value);
        timeoutingExecutor.execute(runnable);
    }

    @Override
    public boolean remove(K key)
    {
        Callable<Boolean> callable = () -> target.remove(key);
        Boolean result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void removeAll()
    {
        Runnable runnable = target::removeAll;
        timeoutingExecutor.execute(runnable);
    }
}