package com.github.mizool.exception;

/**
 * Thrown when one or more objects are not found.
 */
public class ObjectNotFoundException extends RuntimeException
{
    public ObjectNotFoundException()
    {
        super();
    }

    public ObjectNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ObjectNotFoundException(String message)
    {
        super(message);
    }

    public ObjectNotFoundException(Throwable cause)
    {
        super(cause);
    }
}