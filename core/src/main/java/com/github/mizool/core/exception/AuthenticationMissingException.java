package com.github.mizool.core.exception;

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
}
