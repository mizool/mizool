package com.github.mizool.technology.jackson.jaxrs;

import org.kohsuke.MetaInfServices;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import jakarta.ws.rs.client.Client;

@MetaInfServices(Module.class)
public class ClientModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        super.configure();
        bind(Client.class).toInstance(ClientFactory.create());
    }
}
