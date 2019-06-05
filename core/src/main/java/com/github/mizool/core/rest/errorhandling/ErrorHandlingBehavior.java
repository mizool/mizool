package com.github.mizool.core.rest.errorhandling;

public interface ErrorHandlingBehavior
{
    Class<? extends Throwable> getThrowableClass();

    boolean includeErrorId();

    boolean includeDetails();

    int getStatusCode();

    LogLevel getMessageLogLevel();

    LogLevel getStackTraceLogLevel();
}
