package com.github.mizool.technology.rest.errorhandling;

public interface ErrorHandlingBehavior
{
    Class<? extends Throwable> getThrowableClass();

    boolean includeErrorId();

    boolean includeDetails();

    int getStatusCode();

    LogLevel getMessageLogLevel();

    LogLevel getStackTraceLogLevel();
}
