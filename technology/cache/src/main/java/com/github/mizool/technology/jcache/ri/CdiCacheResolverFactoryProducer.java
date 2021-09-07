package com.github.mizool.technology.jcache.ri;

import javax.cache.CacheManager;
import javax.cache.annotation.CacheResolverFactory;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jsr107.ri.annotations.DefaultCacheResolverFactory;
import org.jsr107.ri.annotations.cdi.CacheResolverFactoryProducer;
import org.jsr107.ri.annotations.cdi.UsedByDefault;

/**
 * Allows to plug a CDI built {@link CacheManager} into the reference implementation.<br>
 * <br>
 * The default implementation retrieves the {@link CacheManager} in a static way.
 */
class CdiCacheResolverFactoryProducer extends CacheResolverFactoryProducer
{
    private final CacheManager cacheManager;

    @Inject
    CdiCacheResolverFactoryProducer(CacheManager cacheManager)
    {
        this.cacheManager = cacheManager;
    }

    @Override
    @Produces
    @UsedByDefault
    @Specializes
    public CacheResolverFactory produce()
    {
        return new DefaultCacheResolverFactory(cacheManager);
    }
}
