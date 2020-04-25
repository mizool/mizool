/*
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.rest.errorhandling;

import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

@Slf4j
@VisibleForTesting
public class GenericErrorMapper
{
    @VisibleForTesting
    public static final String GLOBAL_PROPERTY_KEY = "GLOBAL";

    private final ErrorHandlingBehaviorCatalog errorHandlingBehaviorCatalog;

    public GenericErrorMapper()
    {
        errorHandlingBehaviorCatalog = new ErrorHandlingBehaviorCatalog();
    }

    public ErrorResponse handleErrorAccordingToBehaviour(Throwable t)
    {
        ErrorResponse result = null;
        Optional<ErrorHandlingBehavior> behaviorOptional = errorHandlingBehaviorCatalog.lookup(t);
        if (behaviorOptional.isPresent())
        {
            ErrorHandlingBehavior behaviour = behaviorOptional.get();
            logError(t, behaviour);

            Map<String, String> parameters = null;
            if (behaviour.includeDetails())
            {
                parameters = createExceptionParameters(t);
            }

            ErrorDto error = ErrorDto.createGenericError(parameters);
            if (behaviour.includeErrorId())
            {
                error = new ErrorDto(t.getClass().getName(), parameters);
            }
            ErrorMessageDto errorMessage = createErrorMessageDto(error);
            result = new ErrorResponse(behaviour.getStatusCode(), errorMessage);
        }
        return result;
    }

    public ErrorResponse handleUndefinedError(Throwable throwable)
    {
        log.error("Unhandled error", throwable);
        Map<String, String> parameters = createExceptionParameters(throwable);

        ErrorDto error = ErrorDto.createGenericError(parameters);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    private void logError(Throwable t, ErrorHandlingBehavior behaviour)
    {
        Throwable rootCause = determineRootCause(t);
        if (rootCause != t)
        {
            behaviour.getMessageLogLevel().log(log, "{} - {}", t.getMessage(), rootCause.getMessage());
        }
        else
        {
            behaviour.getMessageLogLevel().log(log, t.getMessage());
        }
        behaviour.getStackTraceLogLevel().log(log, t.getMessage(), t);
    }

    private Throwable determineRootCause(Throwable t)
    {
        Throwable rootCause = t;
        while (rootCause.getCause() != null)
        {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private Map<String, String> createExceptionParameters(Throwable throwable)
    {
        Map<String, String> parameters = Maps.newHashMap();
        parameters.put("Exception", throwable.getMessage());
        parameters.put("RootCause", Throwables.getRootCause(throwable).getMessage());
        return parameters;
    }

    private ErrorMessageDto createErrorMessageDto(ErrorDto error)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return ErrorMessageDto.builder().errors(errors.asMap()).build();
    }
}
