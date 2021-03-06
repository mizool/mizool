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

import com.github.mizool.technology.jcache.common.AbstractDelegatingCache;

class NoOpCache<K, V> extends AbstractDelegatingCache<K, V>
{
    public NoOpCache()
    {
        super(null);
    }

    @Override
    public V get(K key)
    {
        return null;
    }

    @Override
    public void put(K key, V value)
    {
        // No-op
    }

    @Override
    public boolean remove(K key)
    {
        return false;
    }

    @Override
    public void removeAll()
    {
        // No-op
    }
}