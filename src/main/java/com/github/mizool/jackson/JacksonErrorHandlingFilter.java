package com.github.mizool.jackson;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizool.rest.errorhandling.AbstractErrorHandlingFilter;
import com.github.mizool.rest.errorhandling.ErrorMessageDto;
import com.google.common.base.Charsets;

@Singleton
public class JacksonErrorHandlingFilter extends AbstractErrorHandlingFilter
{
    @Inject
    private ObjectMapper objectMapper;

    @Override
    public byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto)
    {
        byte[] result;
        try
        {
            String errorMessage = objectMapper.writeValueAsString(errorMessageDto);
            result = errorMessage.getBytes(Charsets.UTF_8);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("Could not deserialize errorMessageDto");
        }
        return result;
    }
}