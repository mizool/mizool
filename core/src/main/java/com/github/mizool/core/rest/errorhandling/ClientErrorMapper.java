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

import static com.github.mizool.core.rest.errorhandling.GenericErrorMapper.GLOBAL_PROPERTY_KEY;

import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.MethodNotAllowedException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Slf4j
public class ClientErrorMapper
{
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
        Class<? extends Exception> errorClass = defaultErrorClass;
        if (statusCode == HttpStatus.METHOD_NOT_ALLOWED)
        {
            errorClass = MethodNotAllowedException.class;
        }
        return errorClass;
    }

    private ErrorMessageDto createErrorMessageDto(ErrorDto error)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return ErrorMessageDto.builder().errors(errors.asMap()).build();
    }
}
