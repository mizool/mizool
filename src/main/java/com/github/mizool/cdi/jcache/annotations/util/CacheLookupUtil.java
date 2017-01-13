/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle America Incorporated
 *  Copyright 2015-2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.cdi.jcache.annotations.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheResolverFactory;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil;
import com.github.mizool.cdi.jcache.annotations.common.DefaultCacheKeyGenerator;
import com.github.mizool.cdi.jcache.annotations.common.DefaultCacheResolverFactory;
import com.github.mizool.cdi.jcache.annotations.common.InternalCacheInvocationContext;
import com.github.mizool.cdi.jcache.annotations.common.InternalCacheKeyInvocationContext;
import com.github.mizool.cdi.jcache.annotations.common.StaticCacheInvocationContext;
import com.github.mizool.cdi.jcache.annotations.common.StaticCacheKeyInvocationContext;

/**
 * Utility used by all annotations to lookup the {@link javax.cache.annotation.CacheResolver} and {@link
 * CacheKeyGenerator} for a given method.
 *
 * @author Rick Hightower
 * @since 1.0
 */
public class CacheLookupUtil extends AbstractCacheLookupUtil<InvocationContext>
{
    @Inject
    private BeanManagerUtil beanManagerUtil;

    private CacheKeyGenerator defaultCacheKeyGenerator = new DefaultCacheKeyGenerator();
    private CacheResolverFactory defaultCacheResolverFactory = new DefaultCacheResolverFactory();

    /*
     * Annoation type cannot be known at compile time so ignore the warning
     *
     * (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#createCacheKeyInvocationContextImpl(javax.cache.annotation.impl.StaticCacheKeyInvocationContext, java.lang.Object)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected InternalCacheKeyInvocationContext<? extends Annotation> createCacheKeyInvocationContextImpl(
        StaticCacheKeyInvocationContext<? extends Annotation> staticCacheKeyInvocationContext,
        InvocationContext invocation)
    {
        return new CdiCacheKeyInvocationContextImpl(staticCacheKeyInvocationContext, invocation);
    }

    /*
     * Annoation type cannot be known at compile time so ignore the warning
     *
     * (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#createCacheInvocationContextImpl
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected InternalCacheInvocationContext<? extends Annotation> createCacheInvocationContextImpl(
        StaticCacheInvocationContext<? extends Annotation> staticCacheInvocationContext, InvocationContext invocation)
    {
        return new CdiCacheInvocationContextImpl(staticCacheInvocationContext, invocation);
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#getTarget(java.lang.Object)
     */
    @Override
    protected Class<?> getTargetClass(InvocationContext invocation)
    {
        return invocation.getMethod().getDeclaringClass();
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#getMethod(java.lang.Object)
     */
    @Override
    protected Method getMethod(InvocationContext invocation)
    {
        return invocation.getMethod();
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#getObjectByType(java.lang.Class)
     */
    @Override
    protected <T> T getObjectByType(Class<T> type)
    {
        return beanManagerUtil.getBeanByType(type);
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#getDefaultCacheKeyGenerator()
     */
    @Override
    protected CacheKeyGenerator getDefaultCacheKeyGenerator()
    {
        return this.defaultCacheKeyGenerator;
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheLookupUtil#getDefaultCacheResolverFactory()
     */
    @Override
    protected CacheResolverFactory getDefaultCacheResolverFactory()
    {
        return this.defaultCacheResolverFactory;
    }
}
