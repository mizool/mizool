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

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;

public class ClientProvider
{
    @Produces
    @Singleton
    public Client produce()
    {
        Client client = ClientBuilder.newClient().register(JacksonFeature.class)
            // the JacksonFeature kills our custom ObjectMapper, so we have to register it afterwards
            .register(ClientCustomObjectMapperProvider.class);

        return client;
    }
}