package com.github.mizool.core.exception;

/**
 * Thrown when the request is malformed on the syntax level, e.g. mandatory properties or parameters are not set.
 */
public class BadRequestException extends RuntimeException
{
    public BadRequestException()
    {
        super();
    }

    public BadRequestException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BadRequestException(String message)
    {
        super(message);
    }

    public BadRequestException(Throwable cause)
    {
        super(cause);
    }
}
