package com.github.mizool.core.exception;

public class ServiceUnavailableException extends RuntimeException
{
    public ServiceUnavailableException()
    {
    }

    public ServiceUnavailableException(String message)
    {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ServiceUnavailableException(Throwable cause)
    {
        super(cause);
    }
}
