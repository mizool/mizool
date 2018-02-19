package com.github.mizool.core.rest.errorhandling;

import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

class ErrorMapper
{
    private static final String GLOBAL_PROPERTY_KEY = "GLOBAL";

    public Map<String, String> createExceptionParameters(Throwable throwable)
    {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("Exception", throwable.getMessage());
        parameters.put("RootCause", Throwables.getRootCause(throwable).getMessage());
        return parameters;
    }

    public ErrorMessageDto createErrorMessageDto(ErrorDto error)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return ErrorMessageDto.builder().errors(errors.asMap()).build();
    }
}
