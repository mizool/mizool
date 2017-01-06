package com.github.mizool.exception;

public class AuthenticationRejectedException extends RuntimeException
{
    public AuthenticationRejectedException()
    {
    }

    public AuthenticationRejectedException(String message)
    {
        super(message);
    }

    public AuthenticationRejectedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AuthenticationRejectedException(Throwable cause)
    {
        super(cause);
    }

    public AuthenticationRejectedException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
