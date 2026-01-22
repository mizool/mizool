package com.github.mizool.core.rest.errorhandling;

import org.kohsuke.MetaInfServices;

@MetaInfServices
public class MoepExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return MoepException.class;
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
        return 112;
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
