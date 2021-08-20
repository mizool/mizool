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
