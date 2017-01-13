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

package com.github.mizool.cdi.jcache.annotations.common;

import javax.cache.annotation.CacheResolver;
import javax.cache.annotation.CacheResult;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public interface StaticCacheResultInvocationContext extends StaticCacheInvocationContext<CacheResult> {

  /**
   * @return The {@link javax.cache.annotation.CacheResolver} to use to get the cache for this method
   */
  CacheResolver getExceptionCacheResolver();
}
