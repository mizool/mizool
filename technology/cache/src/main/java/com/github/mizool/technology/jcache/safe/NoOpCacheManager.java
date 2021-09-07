package com.github.mizool.technology.jcache.safe;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.NonDefault;
import com.github.mizool.technology.jcache.common.AbstractDelegatingCacheManager;

@Slf4j
@NonDefault
class NoOpCacheManager extends AbstractDelegatingCacheManager
{
    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration)
        throws IllegalArgumentException
    {
        return new NoOpCache<>();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName)
    {
        return new NoOpCache<>();
    }
}
