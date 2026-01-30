package com.github.mizool.technology.rest.errorhandling.behavior;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.MethodNotAllowedException;
import com.github.mizool.technology.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.technology.rest.errorhandling.HttpStatus;
import com.github.mizool.technology.rest.errorhandling.LogLevel;

@MetaInfServices
public class MethodNotAllowedExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return MethodNotAllowedException.class;
    }

    @Override
    public boolean includeErrorId()
    {
        return true;
    }

    @Override
    public boolean includeDetails()
    {
        return false;
    }

    @Override
    public int getStatusCode()
    {
        return HttpStatus.METHOD_NOT_ALLOWED;
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
