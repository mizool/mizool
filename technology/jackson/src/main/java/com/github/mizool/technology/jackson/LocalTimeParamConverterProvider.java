/*
 *  Copyright 2017-2018 incub8 Software Labs GmbH
 *  Copyright 2017-2018 protel Hotelsoftware GmbH
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
package com.github.mizool.technology.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class LocalTimeParamConverterProvider implements ParamConverterProvider
{
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        ParamConverter<T> result = null;

        if (rawType.equals(LocalTime.class))
        {
            result = getParamConverter();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> ParamConverter<T> getParamConverter()
    {
        return (ParamConverter<T>) new LocalTimeParamConverter();
    }
}