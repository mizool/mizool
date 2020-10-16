/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.concurrent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
class ExceptionTests
{
    public Throwable instantiateThrowable(Class<? extends Throwable> throwableClass, String message)
    {
        try
        {
            return getStringArgConstructor(throwableClass).newInstance(message);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new CodeInconsistencyException("Could not instantiate " + throwableClass.getCanonicalName(), e);
        }
    }

    private <T> Constructor<T> getStringArgConstructor(Class<T> theClass)
    {
        try
        {
            return theClass.getConstructor(String.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new CodeInconsistencyException("Could not find String arg constructor for " +
                theClass.getCanonicalName(), e);
        }
    }
}
