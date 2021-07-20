package com.github.mizool.technology.jackson.jaxrs;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

public class ClientProducer
{
    @Produces
    @Singleton
    public Client produce()
    {
        return ClientFactory.create();
    }
}
