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
package com.github.mizool.core.rest.errorhandling;

import java.util.Map;

import javax.inject.Singleton;

import com.github.mizool.core.MetaInfServices;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@Singleton
class ExceptionCatalog
{
    private final Map<Class<? extends Throwable>, WhiteListEntry> catalog;

    protected ExceptionCatalog()
    {
        Iterable<WhiteList> whiteLists = MetaInfServices.instances(WhiteList.class);

        ImmutableMap.Builder<Class<? extends Throwable>, WhiteListEntry> catalogBuilder = ImmutableMap.builder();
        whiteLists.forEach(whiteList -> whiteList.getEntries()
            .forEach(entry -> catalogBuilder.put(entry.getExceptionClass(), entry)));

        catalog = catalogBuilder.build();
    }

    public Optional<WhiteListEntry> lookup(Throwable t)
    {
        WhiteListEntry whiteListEntry = catalog.get(t.getClass());
        if (whiteListEntry == null)
        {
            for (Class<? extends Throwable> exceptionClass : catalog.keySet())
            {
                if (exceptionClass.isAssignableFrom(t.getClass()))
                {
                    whiteListEntry = catalog.get(exceptionClass);
                }
            }
        }
        Optional<WhiteListEntry> result = Optional.fromNullable(whiteListEntry);
        return result;
    }
}