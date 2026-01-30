package com.github.mizool.technology.jackson.behavior;

import java.time.format.DateTimeParseException;

import org.kohsuke.MetaInfServices;

import com.github.mizool.technology.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.technology.rest.errorhandling.HttpStatus;
import com.github.mizool.technology.rest.errorhandling.LogLevel;

@MetaInfServices
public class DateTimeParseExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return DateTimeParseException.class;
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
        return HttpStatus.BAD_REQUEST;
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
