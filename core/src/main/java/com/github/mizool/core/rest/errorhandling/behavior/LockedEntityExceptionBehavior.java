package com.github.mizool.core.rest.errorhandling.behavior;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.exception.LockedEntityException;
import com.github.mizool.core.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.core.rest.errorhandling.HttpStatus;
import com.github.mizool.core.rest.errorhandling.LogLevel;

@MetaInfServices
public class LockedEntityExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return LockedEntityException.class;
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
        return HttpStatus.LOCKED;
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
