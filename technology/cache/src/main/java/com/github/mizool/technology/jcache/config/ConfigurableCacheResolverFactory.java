/**
 * Copyright 2011-2013 Terracotta, Inc.
 * Copyright 2011-2013 Oracle America Incorporated
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

import java.lang.annotation.Annotation;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheMethodDetails;
import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.annotation.CacheResult;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

import org.jsr107.ri.annotations.DefaultCacheResolver;

/**
 * Default {@link javax.cache.annotation.CacheResolverFactory} that uses the default {@link javax.cache.CacheManager} and finds the {@link javax.cache.Cache}
 * using {@link javax.cache.CacheManager#getCache(String)}. Returns a {@link DefaultCacheResolver} that wraps the found
 * {@link javax.cache.Cache}
 *
 * @author Eric Dalquist
 * @author Rick Hightower
 * @since 1.0
 */
public class ConfigurableCacheResolverFactory implements CacheResolverFactory
{
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final CacheManager cacheManager;
    private final CacheConfiguration cacheConfiguration;

    /**
     * Constructs the resolver
     *
     * @param cacheManager the cache manager to use
     */
    public ConfigurableCacheResolverFactory(CacheManager cacheManager, CacheConfiguration cacheConfiguration)
    {
        this.cacheManager = cacheManager;
        this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Constructs the resolver
     */
    public ConfigurableCacheResolverFactory()
    {
        CachingProvider provider = Caching.getCachingProvider();
        this.cacheManager = provider.getCacheManager(provider.getDefaultURI(), provider.getDefaultClassLoader());
        this.cacheConfiguration = new CacheConfiguration();
    }

    /* (non-Javadoc)
     * @see javax.cache.annotation.CacheResolverFactory#getCacheResolver(javax.cache.annotation.CacheMethodDetails)
     */
    @Override
    public CacheResolver getCacheResolver(CacheMethodDetails<? extends Annotation> cacheMethodDetails)
    {
        final String cacheName = cacheMethodDetails.getCacheName();
        Cache<?, ?> cache = this.cacheManager.getCache(cacheName);

        if (cache == null)
        {
            logger.warning("No Cache named '" +
                cacheName +
                "' was found in the CacheManager, a pre-configured cache will be created.");
            Configuration<Object, Object> configuration = cacheConfiguration.getConfiguration(cacheName);
            cacheManager.createCache(cacheName, configuration);
            cache = cacheManager.getCache(cacheName);
        }

        return new DefaultCacheResolver(cache);
    }

    @Override
    public CacheResolver getExceptionCacheResolver(CacheMethodDetails<CacheResult> cacheMethodDetails)
    {
        final CacheResult cacheResultAnnotation = cacheMethodDetails.getCacheAnnotation();
        final String exceptionCacheName = cacheResultAnnotation.exceptionCacheName();
        if (exceptionCacheName == null || exceptionCacheName.trim().length() == 0)
        {
            throw new IllegalArgumentException("Can only be called when CacheResult.exceptionCacheName() is specified");
        }

        Cache<?, ?> cache = cacheManager.getCache(exceptionCacheName);

        if (cache == null)
        {
            logger.warning("No Cache named '" +
                exceptionCacheName +
                "' was found in the CacheManager, a pre-configured cache will be created.");
            Configuration<Object, Object> configuration = cacheConfiguration.getConfiguration(exceptionCacheName);
            cacheManager.createCache(exceptionCacheName, configuration);
            cache = cacheManager.getCache(exceptionCacheName);
        }

        return new DefaultCacheResolver(cache);
    }
}
