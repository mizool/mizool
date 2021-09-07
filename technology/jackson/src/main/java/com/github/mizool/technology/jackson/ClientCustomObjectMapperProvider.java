package com.github.mizool.technology.jackson;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

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
