package com.github.mizool.technology.jackson.behavior;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.MetaInfServices;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.mizool.core.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.core.rest.errorhandling.LogLevel;

@MetaInfServices
public class UnrecognizedPropertyExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return UnrecognizedPropertyException.class;
    }

    @Override
    public boolean includeErrorId()
    {
        return true;
    }

    @Override
    public boolean includeDetails()
    {
        return true;
    }

    @Override
    public int getStatusCode()
    {
        return HttpServletResponse.SC_BAD_REQUEST;
    }

    @Override
    public LogLevel getMessageLogLevel()
    {
        return LogLevel.NONE;
    }

    @Override
    public LogLevel getStackTraceLogLevel()
    {
        return LogLevel.DEBUG;
    }
}
