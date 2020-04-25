/*
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import com.github.mizool.core.MetaInfServices;
import com.google.common.collect.ImmutableMap;

@Singleton
class ErrorHandlingBehaviorCatalog
{
    private static final String CORE_PACKAGE = ErrorHandlingBehaviorCatalog.class.getPackage().getName();

    private final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog;

    public ErrorHandlingBehaviorCatalog()
    {
        Iterable<ErrorHandlingBehavior> behaviours = MetaInfServices.instances(ErrorHandlingBehavior.class);
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog = new HashMap<>();
        behaviours.forEach(behaviour -> {
            String behaviorPackage = behaviour.getClass().getPackage().getName();
            Class<? extends Throwable> throwableClass = behaviour.getThrowableClass();
            if (!catalog.containsKey(throwableClass) || !behaviorPackage.startsWith(CORE_PACKAGE))
            {
                catalog.put(throwableClass, behaviour);
            }
        });

        this.catalog = ImmutableMap.copyOf(catalog);
    }

    public Optional<ErrorHandlingBehavior> lookup(Throwable t)
    {
        ErrorHandlingBehavior behaviour = catalog.get(t.getClass());
        if (behaviour == null)
        {
            for (Class<? extends Throwable> throwableClass : catalog.keySet())
            {
                if (throwableClass.isAssignableFrom(t.getClass()))
                {
                    behaviour = catalog.get(throwableClass);
                }
            }
        }
        Optional<ErrorHandlingBehavior> result = Optional.ofNullable(behaviour);
        return result;
    }
}
