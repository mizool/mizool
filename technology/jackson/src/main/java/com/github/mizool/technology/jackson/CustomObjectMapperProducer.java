package com.github.mizool.technology.jackson;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomObjectMapperProducer
{
    @Produces
    @Singleton
    public ObjectMapper produce()
    {
        return CustomObjectMapperFactory.create();
    }
}
