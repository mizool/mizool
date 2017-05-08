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
package com.github.mizool.technology.jackson;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class ClientCustomObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    // jax rs client has a problem injecting the ObjectMapper from our producer, so we get it manually
    private final CustomObjectMapperProducer customObjectMapperProducer = new CustomObjectMapperProducer();

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return customObjectMapperProducer.produce();
    }
}