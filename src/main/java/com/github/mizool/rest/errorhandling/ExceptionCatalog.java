/**
 *  Copyright 2017 incub8 Software Labs GmbH
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
package com.github.mizool.rest.errorhandling;

import java.util.Map;

import javax.inject.Singleton;

import com.github.mizool.MetaInfServices;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@Singleton
class ExceptionCatalog
{
    private final Map<String, Integer> catalog;

    protected ExceptionCatalog()
    {
        Iterable<WhiteList> whiteLists = MetaInfServices.instances(WhiteList.class);

        ImmutableMap.Builder<String, Integer> catalogBuilder = ImmutableMap.builder();

        for (WhiteList whiteList : whiteLists)
        {
            catalogBuilder.putAll(whiteList.getEntries());
        }

        catalog = catalogBuilder.build();
    }

    public Optional<Integer> lookup(Throwable t)
    {
        String exceptionClassName = t.getClass().getName();
        Integer statusCode = catalog.get(exceptionClassName);
        Optional<Integer> result = Optional.fromNullable(statusCode);
        return result;
    }
}