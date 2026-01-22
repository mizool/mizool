package com.github.mizool.core.rest.errorhandling;

import org.kohsuke.MetaInfServices;

@MetaInfServices
public class OriginalExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return OriginalException.class;
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
        return 666;
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
