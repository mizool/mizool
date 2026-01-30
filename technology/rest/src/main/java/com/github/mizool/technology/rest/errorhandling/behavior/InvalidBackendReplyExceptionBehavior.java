package com.github.mizool.technology.rest.errorhandling.behavior;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.InvalidBackendReplyException;
import com.github.mizool.technology.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.technology.rest.errorhandling.HttpStatus;
import com.github.mizool.technology.rest.errorhandling.LogLevel;

@MetaInfServices
public class InvalidBackendReplyExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return InvalidBackendReplyException.class;
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
        return HttpStatus.BAD_GATEWAY;
    }

    @Override
    public LogLevel getMessageLogLevel()
    {
        return LogLevel.WARN;
    }

    @Override
    public LogLevel getStackTraceLogLevel()
    {
        return LogLevel.DEBUG;
    }
}
