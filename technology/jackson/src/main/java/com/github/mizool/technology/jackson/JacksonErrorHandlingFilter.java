package com.github.mizool.technology.jackson;

import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.rest.errorhandling.ErrorMessageDto;
import com.github.mizool.technology.web.AbstractErrorHandlingFilter;

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
            result = errorMessage.getBytes(StandardCharsets.UTF_8);
        }
        catch (JsonProcessingException e)
        {
            throw new CodeInconsistencyException("Could not serialize errorMessageDto", e);
        }
        return result;
    }
}
