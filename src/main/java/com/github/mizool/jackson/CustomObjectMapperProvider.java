package com.github.mizool.jackson;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class CustomObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper objectMapper;

    public CustomObjectMapperProvider()
    {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        objectMapper.registerModule(new LocalDateTimeModule());
        objectMapper.registerModule(new ZonedDateTimeModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return objectMapper;
    }
}