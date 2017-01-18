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
    protected abstract byte[] getErrorMessageJsonBytes(ErrorMessageDto errorMessageDto);

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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }
}