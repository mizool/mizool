/**
 *  Copyright 2011-2013 Terracotta, Inc.
 *  Copyright 2011-2013 Oracle America Incorporated
 *  Copyright 2015-2017 incub8 Software Labs GmbH
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

import javax.annotation.Priority;
import javax.cache.annotation.CacheRemoveAll;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.github.mizool.cdi.jcache.annotations.common.AbstractCacheRemoveAllInterceptor;

/**
 * Interceptor for {@link CacheRemoveAll}
 *
 * @author Rick Hightower
 * @author Eric Dalquist
 * @since 1.0
 */
@CacheRemoveAll
@Interceptor
@Priority(Interceptor.Priority.APPLICATION - 100)
public class CacheRemoveAllInterceptor extends AbstractCacheRemoveAllInterceptor<InvocationContext>
{
    @Inject
    private CacheLookupUtil lookup;

    /**
     * @param invocationContext The intercepted invocation
     *
     * @return The result from {@link javax.interceptor.InvocationContext#proceed()}
     *
     * @throws Throwable likely {@link javax.interceptor.InvocationContext#proceed()} threw an exception
     */
    @AroundInvoke
    public Object cacheRemoveAll(InvocationContext invocationContext) throws Throwable
    {
        return this.cacheRemoveAll(this.lookup, invocationContext);
    }

    /* (non-Javadoc)
     * @see com.github.mizool.cdi.jcache.annotations.common.AbstractCacheInterceptor#proceed(java.lang.Object)
     */
    @Override
    protected Object proceed(InvocationContext invocation) throws Exception
    {
        return invocation.proceed();
    }
}