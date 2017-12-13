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
package com.github.mizool.technology.jcache.ri;

import javax.cache.CacheManager;
import javax.cache.annotation.CacheResolverFactory;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.jsr107.ri.annotations.cdi.CacheResolverFactoryProducer;
import org.jsr107.ri.annotations.cdi.UsedByDefault;

import com.github.mizool.technology.jcache.config.CacheConfiguration;
import com.github.mizool.technology.jcache.config.ConfigurableCacheResolverFactory;

/**
 * Allows to plug a CDI built {@link CacheManager} into the reference implementation.<br>
 * <br>
 * The default implementation retrieves the {@link CacheManager} in a static way.
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
class CdiCacheResolverFactoryProducer extends CacheResolverFactoryProducer
{
    private final CacheManager cacheManager;
    private final CacheConfiguration cacheConfiguration;

    @Override
    @Produces
    @UsedByDefault
    @Specializes
    public CacheResolverFactory produce()
    {
        return new ConfigurableCacheResolverFactory(cacheManager, cacheConfiguration);
    }
}