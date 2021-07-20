package com.github.mizool.core.rest.errorhandling;

import java.util.Collection;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Value
@Builder(toBuilder = true)
public class ErrorMessageDto
{
    private final Map<String, Collection<ErrorDto>> errors;

    public ErrorMessageDto combineWith(ErrorMessageDto other)
    {
        SetMultimap<String, ErrorDto> combined = HashMultimap.create();

        errors.forEach(combined::putAll);
        other.getErrors().forEach(combined::putAll);

        return toBuilder().errors(combined.asMap()).build();
    }
}
