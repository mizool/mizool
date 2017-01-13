/**
 *  Copyright 2017 incub8 Software Labs GmbH
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
package com.github.mizool.jackson;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class CustomObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper objectMapper;

    public CustomObjectMapperProvider()
    {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        objectMapper.registerModule(new LocalDateTimeModule());
        objectMapper.registerModule(new ZonedDateTimeModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return objectMapper;
    }
}