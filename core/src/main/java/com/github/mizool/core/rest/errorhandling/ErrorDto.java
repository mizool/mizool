package com.github.mizool.core.rest.errorhandling;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ErrorDto
{
    public static final String GENERIC_ERROR_ID = "generic";

    static ErrorDto createGenericError()
    {
        return createGenericError(null);
    }

    static ErrorDto createGenericError(Map<String, String> parameters)
    {
        return new ErrorDto(GENERIC_ERROR_ID, parameters);
    }

    private final String errorId;
    private final Map<String, String> parameters;
}
