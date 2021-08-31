package com.github.mizool.core.rest.errorhandling;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

@Value
@Builder(toBuilder = true)
public class ErrorMessageDto
{
    Map<String, Collection<ErrorDto>> errors;
    Map<String, Object> globalParameters;

    public ErrorMessageDto combineWith(ErrorMessageDto other)
    {
        return toBuilder().errors(combineErrors(other).asMap())
            .globalParameters(combineGlobalParameters(other))
            .build();
    }

    private SetMultimap<String, ErrorDto> combineErrors(ErrorMessageDto other)
    {
        SetMultimap<String, ErrorDto> combined = HashMultimap.create();
        errors.forEach(combined::putAll);
        other.getErrors()
            .forEach(combined::putAll);
        return Multimaps.unmodifiableSetMultimap(combined);
    }

    private Map<String, Object> combineGlobalParameters(ErrorMessageDto other)
    {
        Map<String, Object> combined = Maps.newHashMap();
        combined.putAll(globalParameters);
        combined.putAll(other.getGlobalParameters());
        return Collections.unmodifiableMap(combined);
    }
}