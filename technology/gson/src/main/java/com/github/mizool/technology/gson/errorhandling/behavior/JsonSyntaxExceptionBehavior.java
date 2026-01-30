package com.github.mizool.technology.gson.errorhandling.behavior;

import org.kohsuke.MetaInfServices;

import com.github.mizool.technology.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.technology.rest.errorhandling.LogLevel;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServletResponse;

@MetaInfServices
public class JsonSyntaxExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return JsonSyntaxException.class;
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
