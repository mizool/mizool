package com.github.mizool.core.rest.errorhandling;

import static com.github.mizool.core.rest.errorhandling.GenericErrorMapper.GLOBAL_PROPERTY_KEY;

import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.MethodNotAllowedException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Slf4j
public class ClientErrorMapper
{
    public ErrorResponse handleClientError(ClientErrorException e)
    {
        log.debug("Client error", e);
        int statusCode = e.getResponse()
            .getStatus();
        Class<? extends Exception> errorClass = determineErrorClass(statusCode, e.getClass());

        ErrorDto error = new ErrorDto(errorClass.getName(), null);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(statusCode, errorMessage);
    }

    private Class<? extends Exception> determineErrorClass(int statusCode, Class<? extends Exception> defaultErrorClass)
    {
        Class<? extends Exception> errorClass = defaultErrorClass;
        if (statusCode == HttpStatus.METHOD_NOT_ALLOWED)
        {
            errorClass = MethodNotAllowedException.class;
        }
        return errorClass;
    }

    private ErrorMessageDto createErrorMessageDto(ErrorDto error)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return ErrorMessageDto.builder()
            .errors(errors.asMap())
            .build();
    }
}