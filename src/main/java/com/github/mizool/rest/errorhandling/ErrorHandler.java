/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.rest.errorhandling;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.exception.MethodNotAllowedException;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

@Slf4j
public class ErrorHandler
{
    private static final String GLOBAL_PROPERTY_KEY = "GLOBAL";
    private static final int SC_UNPROCESSABLE_ENTITY = 422;

    private final ExceptionCatalog exceptionCatalog;

    public ErrorHandler()
    {
        this.exceptionCatalog = new ExceptionCatalog();
    }

    @Inject
    protected ErrorHandler(ExceptionCatalog exceptionCatalog)
    {
        this.exceptionCatalog = exceptionCatalog;
    }

    public ErrorResponse handle(Throwable throwable)
    {
        ErrorResponse result = null;

        Throwable cursor = throwable;
        while (cursor != null)
        {
            Optional<Integer> statusCode = exceptionCatalog.lookup(cursor);
            if (statusCode.isPresent())
            {
                result = handleWhitelistedException(cursor, statusCode.get());
                break;
            }
            else if (isAssignable(ConstraintViolationException.class, cursor))
            {
                result = handleValidationError((ConstraintViolationException) cursor);
            }
            else if (isAssignable(ClientErrorException.class, cursor))
            {
                result = handleClientError((ClientErrorException) cursor);
            }
            cursor = cursor.getCause();
        }

        if (result == null)
        {
            result = handleUndefinedError(throwable);
        }

        return result;
    }

    private boolean isAssignable(Class<?> throwableClass, Throwable throwable)
    {
        return throwableClass.isAssignableFrom(throwable.getClass());
    }

    private ErrorResponse handleWhitelistedException(Throwable t, int statusCode)
    {
        log.debug("Whitelisted exception", t);
        Map<String, String> parameters = createExceptionParameters(t);
        ErrorDto error = new ErrorDto(t.getClass().getName(), parameters);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(statusCode, errorMessage);
    }

    private ErrorResponse handleValidationError(ConstraintViolationException e)
    {
        log.debug("Validation error", e);
        ListMultimap<String, ErrorDto> errors = ArrayListMultimap.create();
        for (ConstraintViolation<?> violation : e.getConstraintViolations())
        {
            recordViolation(violation, errors);
        }
        ErrorMessageDto errorMessage = createErrorMessageDto(errors);
        return new ErrorResponse(SC_UNPROCESSABLE_ENTITY, errorMessage);
    }

    private ErrorResponse handleClientError(ClientErrorException e)
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

    private void recordViolation(ConstraintViolation<?> violation, ListMultimap<String, ErrorDto> target)
    {
        String errorId = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();

        Path.Node lastProperty = Iterators.getLast(violation.getPropertyPath().iterator());
        String propertyName = lastProperty.getName();

        ErrorDto errorDto = new ErrorDto(errorId, null);
        target.put(propertyName, errorDto);
    }

    private ErrorResponse handleUndefinedError(Throwable throwable)
    {
        log.error("Unhandled error", throwable);
        ErrorResponse result = createUndefinedErrorResponse(throwable);
        return result;
    }

    private ErrorResponse createUndefinedErrorResponse(Throwable throwable)
    {
        Map<String, String> parameters = createExceptionParameters(throwable);
        ErrorDto error = new ErrorDto(ErrorDto.GENERIC_FIELD_KEY, parameters);

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
        ListMultimap<String, ErrorDto> errors = ArrayListMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return createErrorMessageDto(errors);
    }

    private ErrorMessageDto createErrorMessageDto(ListMultimap<String, ErrorDto> errors)
    {
        return new ErrorMessageDto(errors.asMap());
    }
}