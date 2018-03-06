/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.MethodNotAllowedException;
import com.github.mizool.core.exception.RuleViolation;
import com.github.mizool.core.exception.RuleViolationException;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.SetMultimap;

@Slf4j
public class ErrorHandler
{
    private static final int SC_UNPROCESSABLE_ENTITY = 422;

    private final ExceptionCatalog exceptionCatalog;
    private final ErrorMapper errorMapper;

    public ErrorHandler()
    {
        this.exceptionCatalog = new ExceptionCatalog();
        errorMapper = new ErrorMapper();
    }

    @Inject
    protected ErrorHandler(ExceptionCatalog exceptionCatalog)
    {
        this.exceptionCatalog = exceptionCatalog;
        errorMapper = new ErrorMapper();
    }

    public ErrorMessageDto fromPojo(Throwable throwable)
    {
        return this.handle(throwable).getBody();
    }

    public ErrorResponse handle(Throwable throwable)
    {
        ErrorResponse result = null;

        Throwable cursor = throwable;
        while (cursor != null)
        {
            Optional<WhiteListEntry> whiteListEntry = exceptionCatalog.lookup(cursor);
            if (whiteListEntry.isPresent())
            {
                result = handleWhitelistedException(cursor, whiteListEntry.get());
                break;
            }
            else if (isAssignable(ConstraintViolationException.class, cursor))
            {
                result = handleConstraintViolationError((ConstraintViolationException) cursor);
            }
            else if (isAssignable(RuleViolationException.class, cursor))
            {
                result = handleRuleViolationError((RuleViolationException) cursor);
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

    private ErrorResponse handleWhitelistedException(Throwable t, WhiteListEntry whiteListEntry)
    {
        log.debug("Whitelisted exception", t);
        Map<String, String> parameters = null;
        if (whiteListEntry.getShouldIncludeDetails())
        {
            parameters = errorMapper.createExceptionParameters(t);
        }
        ErrorDto error = new ErrorDto(t.getClass().getName(), parameters);
        ErrorMessageDto errorMessage = errorMapper.createErrorMessageDto(error);
        return new ErrorResponse(whiteListEntry.getStatusCode(), errorMessage);
    }

    private ErrorResponse handleConstraintViolationError(ConstraintViolationException e)
    {
        log.debug("Validation error", e);
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        for (ConstraintViolation<?> violation : e.getConstraintViolations())
        {
            recordConstraintViolation(violation, errors);
        }
        ErrorMessageDto errorMessage = ErrorMessageDto.builder().errors(errors.asMap()).build();
        return new ErrorResponse(SC_UNPROCESSABLE_ENTITY, errorMessage);
    }

    private void recordConstraintViolation(ConstraintViolation<?> violation, SetMultimap<String, ErrorDto> target)
    {
        String errorId = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();

        Path.Node lastProperty = Iterators.getLast(violation.getPropertyPath().iterator());
        String propertyName = lastProperty.getName();

        ErrorDto errorDto = new ErrorDto(errorId, null);
        target.put(propertyName, errorDto);
    }

    private ErrorResponse handleRuleViolationError(RuleViolationException e)
    {
        log.debug("Rule violation error", e);
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        for (RuleViolation violation : e.getRuleViolations())
        {
            recordRuleViolation(violation, errors);
        }
        ErrorMessageDto errorMessage = ErrorMessageDto.builder().errors(errors.asMap()).build();
        return new ErrorResponse(SC_UNPROCESSABLE_ENTITY, errorMessage);
    }

    private void recordRuleViolation(RuleViolation violation, SetMultimap<String, ErrorDto> target)
    {
        ErrorDto errorDto = new ErrorDto(violation.getErrorId(), null);
        target.put(violation.getFieldName(), errorDto);
    }

    private ErrorResponse handleClientError(ClientErrorException e)
    {
        log.debug("Client error", e);
        int statusCode = e.getResponse().getStatus();
        Class<? extends Exception> errorClass = determineErrorClass(statusCode, e.getClass());
        ErrorDto error = new ErrorDto(errorClass.getName(), null);
        ErrorMessageDto errorMessage = errorMapper.createErrorMessageDto(error);
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

    private ErrorResponse handleUndefinedError(Throwable throwable)
    {
        log.error("Unhandled error", throwable);
        ErrorResponse result = createUndefinedErrorResponse(throwable);
        return result;
    }

    private ErrorResponse createUndefinedErrorResponse(Throwable throwable)
    {
        Map<String, String> parameters = errorMapper.createExceptionParameters(throwable);
        ErrorDto error = new ErrorDto(ErrorDto.GENERIC_FIELD_KEY, parameters);

        ErrorMessageDto errorMessage = errorMapper.createErrorMessageDto(error);
        return new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }

}