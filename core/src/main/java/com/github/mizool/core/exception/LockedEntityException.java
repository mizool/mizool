package com.github.mizool.core.exception;

/**
 * Thrown when trying to manipulate a locked entity
 */
public class LockedEntityException extends RuntimeException
{
    public LockedEntityException()
    {
        super();
    }

    public LockedEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LockedEntityException(String message)
    {
        super(message);
    }

    public LockedEntityException(Throwable cause)
    {
        super(cause);
    }
}
