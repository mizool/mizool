package com.github.mizool.core.converter;

import java.io.IOException;

import javax.inject.Inject;

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
        String record = null;
        if (pojo != null)
        {
            try
            {
                record = objectMapper.writeValueAsString(pojo);
            }
            catch (JsonProcessingException e)
            {
                throw new StoreLayerException("Error serializing field", e);
            }
        }
        return record;
    }

    public <P> P toPojo(String record, TypeReference<P> type)
    {
        P pojo = null;
        if (record != null)
        {
            try
            {
                pojo = objectMapper.readValue(record, type);
            }
            catch (IOException e)
            {
                throw new StoreLayerException("Error deserializing field", e);
            }
        }
        return pojo;
    }
}