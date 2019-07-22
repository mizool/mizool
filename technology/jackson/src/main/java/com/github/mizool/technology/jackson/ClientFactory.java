package com.github.mizool.technology.jackson;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import lombok.experimental.UtilityClass;

import org.glassfish.jersey.jackson.JacksonFeature;

@UtilityClass
class ClientFactory
{
    public Client create()
    {
        return ClientBuilder.newClient().register(JacksonFeature.class)
            // the JacksonFeature kills our custom ObjectMapper, so we have to register it afterwards
            .register(ClientCustomObjectMapperProvider.class);
    }
}
