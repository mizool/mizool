package com.github.mizool.technology.jackson;

import java.io.IOException;

import jakarta.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizool.core.exception.StoreLayerException;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class JsonConverter
{
    private final ObjectMapper objectMapper;

    public <P> String toRecord(P pojo)
    {
        String result = null;
        if (pojo != null)
        {
            try
            {
                result = objectMapper.writeValueAsString(pojo);
            }
            catch (JsonProcessingException e)
            {
                throw new StoreLayerException("Error serializing field", e);
            }
        }
        return result;
    }

    public <P> P toPojo(String sourceRecord, TypeReference<P> type)
    {
        P pojo = null;
        if (sourceRecord != null)
        {
            try
            {
                pojo = objectMapper.readValue(sourceRecord, type);
            }
            catch (IOException e)
            {
                throw new StoreLayerException("Error deserializing field", e);
            }
        }
        return pojo;
    }
}
