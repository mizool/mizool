/*
 *  Copyright 2017-2020 incub8 Software Labs GmbH
 *  Copyright 2017-2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.rest.errorhandling;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * We need this specialized ExceptionMapper for ConstraintViolation in order to prevent the jersey bean validation
 * from using its own ValidationExceptionMapper.
 * See <a href="https://stackoverflow.com/questions/38681986/how-to-override-a-built-in-exception-mapper-in-jersey-2-23#comment64768295_38681986">here</a> for more details.
 */
@Provider
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
    private final RestExceptionMapper restExceptionMapper;

    @Override
    public Response toResponse(ConstraintViolationException exception)
    {
        return restExceptionMapper.toResponse(exception);
    }
}