package com.github.mizool.technology.jackson;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

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
