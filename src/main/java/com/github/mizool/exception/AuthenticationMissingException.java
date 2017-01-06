package com.github.mizool.exception;

public class AuthenticationMissingException extends RuntimeException
{
    public AuthenticationMissingException()
    {
    }

    public AuthenticationMissingException(String message)
    {
        super(message);
    }

    public AuthenticationMissingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AuthenticationMissingException(Throwable cause)
    {
        super(cause);
    }

    public AuthenticationMissingException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
