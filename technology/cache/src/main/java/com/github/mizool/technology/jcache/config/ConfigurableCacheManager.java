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
package com.github.mizool.technology.jcache.config;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.AbstractDelegatingCacheManager;

/**
 * This class is {@link Vetoed} as it is not intended to be a CDI bean and/or to be injected on its own. It's rather a
 * decorator used in a specific way inside a producer.
 */
@Slf4j
@Vetoed
public class ConfigurableCacheManager extends AbstractDelegatingCacheManager
{
    private final Event<CacheCreation> cacheCreationEvent;

    @Inject
    public ConfigurableCacheManager(@NonNull Event<CacheCreation> cacheCreationEvent)
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