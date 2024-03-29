package com.github.mizool.core.rest.errorhandling;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Value;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

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
        if (errors != null)
        {
            errors.forEach(combined::putAll);
        }
        if (other.getErrors() != null)
        {
            other.getErrors()
                .forEach(combined::putAll);
        }
        return Multimaps.unmodifiableSetMultimap(combined);
    }

    private Map<String, Object> combineGlobalParameters(ErrorMessageDto other)
    {
        Map<String, Object> result = null;

        Map<String, Object> otherGlobalParameters = other.getGlobalParameters();

        if (globalParameters != null && otherGlobalParameters == null)
        {
            result = globalParameters;
        }
        if (globalParameters == null && otherGlobalParameters != null)
        {
            result = otherGlobalParameters;
        }
        if (globalParameters != null && otherGlobalParameters != null)
        {
            Set<String> combinedKeys = Sets.union(globalParameters.keySet(), otherGlobalParameters.keySet());

            result = combinedKeys.stream()
                .collect(ImmutableMap.toImmutableMap(key -> key,
                    key -> resolveValueInOrder(key, otherGlobalParameters, globalParameters)));
        }

        return result;
    }

    private Object resolveValueInOrder(String key, Map<String, Object> first, Map<String, Object> second)
    {
        return first.getOrDefault(key, second.get(key));
    }
}