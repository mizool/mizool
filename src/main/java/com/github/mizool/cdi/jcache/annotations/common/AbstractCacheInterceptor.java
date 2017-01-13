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

package com.github.mizool.cdi.jcache.annotations.common;

/**
 * Base class for cache related interceptors
 *
 * @param <I> The intercepted method invocation
 * @author Eric Dalquist
 * @since 1.0
 */
public abstract class AbstractCacheInterceptor<I> {
  /**
   * Proceed with the invocation
   *
   * @param invocation The intercepted invocation
   * @return The value returned by the invocation
   * @throws Throwable The exception thrown by the invocation, if any
   */
  protected abstract Object proceed(I invocation) throws Throwable;

}
