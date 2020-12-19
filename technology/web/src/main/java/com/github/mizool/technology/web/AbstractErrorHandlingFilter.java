/*
 * Copyright 2017-2020 incub8 Software Labs GmbH
 * Copyright 2017-2020 protel Hotelsoftware GmbH
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
package com.github.mizool.technology.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.rest.errorhandling.ErrorMessageDto;
import com.github.mizool.core.rest.errorhandling.ErrorResponse;
import com.github.mizool.core.rest.errorhandling.ErrorResponseFactory;

@Slf4j
public abstract class AbstractErrorHandlingFilter extends HttpFilterAdapter
{
    private final ErrorResponseFactory errorResponseFactory;

    protected AbstractErrorHandlingFilter()
    {
        this.errorResponseFactory = new ErrorResponseFactory();
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException
    {
        try
        {
            chain.doFilter(request, response);
        }
        catch (@SuppressWarnings("java:S1181") Throwable throwable)
        {
            // We need to catch Throwable so that RuntimeExceptions or Errors don't cause HTML error pages
            sendErrorMessage(throwable, response);
        }
    }

    private void sendErrorMessage(Throwable throwable, HttpServletResponse response) throws IOException
    {
        if (response.isCommitted())
        {
            log.error("Exception during response sending", throwable);
        }
        else
        {
            ErrorResponse errorResponse = errorResponseFactory.handle(throwable);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(errorResponse.getStatusCode());
            response.getOutputStream().write(getErrorMessageJsonBytes(errorResponse.getBody()));
        }
    }

    protected abstract byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto);
}
