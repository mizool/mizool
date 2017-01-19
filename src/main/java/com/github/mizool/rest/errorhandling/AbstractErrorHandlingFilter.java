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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.github.mizool.rest.core.TransactionalResponseWrapper;

public abstract class AbstractErrorHandlingFilter implements Filter
{
    @Inject
    private ErrorHandler errorHandler;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try
        {
            TransactionalResponseWrapper transactionalResponseWrapper = new TransactionalResponseWrapper(httpResponse);
            chain.doFilter(request, transactionalResponseWrapper);
            transactionalResponseWrapper.commit();
        }
        catch (Throwable t)
        {
            sendErrorMessage(t, httpResponse);
        }
    }

    private void sendErrorMessage(Throwable t, HttpServletResponse httpResponse) throws IOException
    {
        ErrorResponse errorResponse = errorHandler.handle(t);

        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(errorResponse.getStatusCode());
        httpResponse.getOutputStream().write(getErrorMessageJsonBytes(errorResponse.getBody()));
    }

    protected abstract byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }
}