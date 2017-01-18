package com.github.mizool.rest.errorhandling;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorMessageDto
{
    private final HashMap<String, Collection<ErrorDto>> errors;

    ErrorMessageDto(Map<String, Collection<ErrorDto>> errors)
    {
        this.errors = new HashMap<>(errors);
    }
}