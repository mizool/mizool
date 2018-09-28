/**
 * Copyright 2017 incub8 Software Labs GmbH
 * Copyright 2017 protel Hotelsoftware GmbH
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
package com.github.mizool.technology.jackson;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.rest.errorhandling.ErrorMessageDto;
import com.github.mizool.technology.web.AbstractErrorHandlingFilter;
import com.google.common.base.Charsets;

@Singleton
public class JacksonErrorHandlingFilter extends AbstractErrorHandlingFilter
{
    @Inject
    private ObjectMapper objectMapper;

    @Override
    public byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto)
    {
        byte[] result;
        try
        {
            String errorMessage = objectMapper.writeValueAsString(errorMessageDto);
            result = errorMessage.getBytes(Charsets.UTF_8);
        }
        catch (JsonProcessingException e)
        {
            throw new CodeInconsistencyException("Could not serialize errorMessageDto", e);
        }
        return result;
    }
}