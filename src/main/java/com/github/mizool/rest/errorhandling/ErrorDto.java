package com.github.mizool.rest.errorhandling;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ErrorDto
{
    public static final String GENERIC_FIELD_KEY = "generic";

    static ErrorDto createGenericError()
    {
        return new ErrorDto(GENERIC_FIELD_KEY, null);
    }

    private final String errorId;
    private final Map<String, String> parameters;
}