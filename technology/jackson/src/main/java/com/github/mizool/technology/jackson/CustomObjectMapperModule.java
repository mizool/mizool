package com.github.mizool.technology.jackson;

import org.kohsuke.MetaInfServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

@MetaInfServices(Module.class)
public class CustomObjectMapperModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        super.configure();
        bind(ObjectMapper.class).toInstance(CustomObjectMapperFactory.create());
    }
}
