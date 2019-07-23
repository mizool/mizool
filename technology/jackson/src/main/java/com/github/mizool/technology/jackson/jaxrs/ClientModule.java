package com.github.mizool.technology.jackson.jaxrs;

import javax.ws.rs.client.Client;

import org.kohsuke.MetaInfServices;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

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
