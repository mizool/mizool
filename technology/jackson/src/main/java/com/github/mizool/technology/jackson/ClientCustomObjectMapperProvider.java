package com.github.mizool.technology.jackson;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class ClientCustomObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return CustomObjectMapperFactory.create();
    }
}
