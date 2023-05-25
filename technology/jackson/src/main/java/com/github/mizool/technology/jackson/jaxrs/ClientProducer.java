package com.github.mizool.technology.jackson.jaxrs;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.ws.rs.client.Client;

public class ClientProducer
{
    @Produces
    @Singleton
    public Client produce()
    {
        return ClientFactory.create();
    }
}
