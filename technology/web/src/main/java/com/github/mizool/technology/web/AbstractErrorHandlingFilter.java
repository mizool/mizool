package com.github.mizool.technology.web;

import java.io.IOException;

import jakarta.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.rest.errorhandling.ErrorMessageDto;
import com.github.mizool.core.rest.errorhandling.ErrorResponse;
import com.github.mizool.core.rest.errorhandling.ErrorResponseFactory;

@Slf4j
public abstract class AbstractErrorHandlingFilter extends HttpFilterAdapter
{
    @Inject
    protected ErrorResponseFactory errorResponseFactory;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException
    {
        try
        {
            invokeNextFilter(request, response, chain);
        }
        catch (@SuppressWarnings("java:S1181") Throwable throwable)
        {
            // We need to catch Throwable so that RuntimeExceptions or Errors don't cause HTML error pages
            sendErrorMessage(throwable, response);
        }
    }

    protected void invokeNextFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        chain.doFilter(request, response);
    }

    protected void sendErrorMessage(Throwable throwable, HttpServletResponse response) throws IOException
    {
        if (response.isCommitted())
        {
            log.error("Exception occurred after response was committed (during body writing)", throwable);
        }
        else
        {
            ErrorResponse errorResponse = errorResponseFactory.handle(throwable);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(errorResponse.getStatusCode());
            response.getOutputStream()
                .write(getErrorMessageJsonBytes(errorResponse.getBody()));
        }
    }

    protected abstract byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto);
}
