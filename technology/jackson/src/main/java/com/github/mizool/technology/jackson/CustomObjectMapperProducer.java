package com.github.mizool.technology.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class CustomObjectMapperProducer
{
    @Produces
    @Singleton
    public ObjectMapper produce()
    {
        return CustomObjectMapperFactory.create();
    }
}
