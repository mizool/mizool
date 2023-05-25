package com.github.mizool.technology.jackson;

import jakarta.inject.Inject;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class CustomObjectMapperProvider implements ContextResolver<ObjectMapper>
{
    private final ObjectMapper objectMapper;

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return objectMapper;
    }
}
