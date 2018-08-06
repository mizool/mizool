/**
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

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.MethodNotAllowedException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

@Slf4j
class ErrorMapper
{
    @VisibleForTesting
    public static final String GLOBAL_PROPERTY_KEY = "GLOBAL";

    public ErrorResponse handleWhitelistedException(Throwable t, WhiteListEntry whiteListEntry)
    {
        log.debug("Whitelisted exception", t);
        Map<String, String> parameters = null;

        if (whiteListEntry.getShouldIncludeDetails())
        {
            parameters = createExceptionParameters(t);
        }
        ErrorDto error = new ErrorDto(t.getClass().getName(), parameters);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(whiteListEntry.getStatusCode(), errorMessage);
    }

    public ErrorResponse handleClientError(ClientErrorException e)
    {
        log.debug("Client error", e);
        int statusCode = e.getResponse().getStatus();
        Class<? extends Exception> errorClass = determineErrorClass(statusCode, e.getClass());

        ErrorDto error = new ErrorDto(errorClass.getName(), null);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(statusCode, errorMessage);
    }

    private Class<? extends Exception> determineErrorClass(int statusCode, Class<? extends Exception> defaultErrorClass)
    {
        Class<? extends Exception> errorClass;
        switch (statusCode)
        {
            case HttpServletResponse.SC_METHOD_NOT_ALLOWED:
                errorClass = MethodNotAllowedException.class;
                break;
            default:
                errorClass = defaultErrorClass;
        }
        return errorClass;
    }

    public ErrorResponse handleUndefinedError(Throwable throwable)
    {
        log.error("Unhandled error", throwable);
        Map<String, String> parameters = createExceptionParameters(throwable);

        ErrorDto error = ErrorDto.createGenericError(parameters);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
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
