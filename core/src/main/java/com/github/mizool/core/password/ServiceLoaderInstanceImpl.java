/**
 * Copyright 2021 incub8 Software Labs GmbH
 * Copyright 2021 protel Hotelsoftware GmbH
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
package com.github.mizool.core.password;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

import com.github.mizool.core.MetaInfServices;

/**
 * Implements Java EE Instance outside CDI environments based on {@link java.util.ServiceLoader}. <br>
 * <br>
 * <h3>Usage</h3>
 * For Guice, add a provider method to your module like this:<br>
 * <pre>{@code
 *     @Provides
 *     @Singleton
 *     private Instance<QuuxInterfaceOrClass> provideQuux()
 *     {
 *         return new ServiceLoaderInstanceImpl<>(QuuxInterfaceOrClass.class);
 *     }
 * }</pre>
 *
 * @param <T> The type of the service to be loaded by {@link java.util.ServiceLoader}
 */
public class ServiceLoaderInstanceImpl<T> implements Instance<T>
{
    private final Iterable<T> instances;

    public ServiceLoaderInstanceImpl(Class<T> targetClass)
    {
        instances = MetaInfServices.instances(targetClass);
    }

    @Override
    public Iterator<T> iterator()
    {
        return instances.iterator();
    }

    @Override
    public T get()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Instance<T> select(Annotation... qualifiers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUnsatisfied()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAmbiguous()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy(T instance)
    {
        throw new UnsupportedOperationException();
    }
}