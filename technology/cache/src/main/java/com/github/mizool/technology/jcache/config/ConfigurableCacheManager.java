package com.github.mizool.technology.jcache.config;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.NonDefault;
import com.github.mizool.technology.jcache.common.AbstractDelegatingCacheManager;

@Slf4j
@NonDefault
public class ConfigurableCacheManager extends AbstractDelegatingCacheManager
{
    private final Event<CacheCreation<?, ?>> cacheCreationEvent;

    @Inject
    public ConfigurableCacheManager(@NonNull Event<CacheCreation<?, ?>> cacheCreationEvent)
    {
        this.cacheCreationEvent = cacheCreationEvent;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(
        String cacheName, C configuration) throws IllegalArgumentException
    {
        /*
         * To avoid generics shenanigans we discard the given configuration and start from scratch.
         * The reference implementation passes an empty configuration anyway.
         */
        MutableConfiguration<K, V> mutableConfiguration = new MutableConfiguration<>();

        CacheCreation<K, V> cacheCreation = new CacheCreation<>(cacheName, mutableConfiguration);
        cacheCreationEvent.fire(cacheCreation);

        return super.createCache(cacheCreation.getCacheName(), cacheCreation.getConfiguration());
    }
}
