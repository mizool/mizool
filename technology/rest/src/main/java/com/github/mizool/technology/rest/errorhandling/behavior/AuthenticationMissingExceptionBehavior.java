package com.github.mizool.technology.rest.errorhandling.behavior;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.AuthenticationMissingException;
import com.github.mizool.technology.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.technology.rest.errorhandling.HttpStatus;
import com.github.mizool.technology.rest.errorhandling.LogLevel;

@MetaInfServices
public class AuthenticationMissingExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return AuthenticationMissingException.class;
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
        return HttpStatus.UNAUTHORIZED;
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
