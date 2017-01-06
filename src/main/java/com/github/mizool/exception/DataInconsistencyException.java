package com.github.mizool.exception;

/**
 * Thrown when the persistent data is in an invalid state and cannot be processed by the application.
 */
public class DataInconsistencyException extends RuntimeException
{
    public DataInconsistencyException()
    {
    }

    public DataInconsistencyException(String message)
    {
        super(message);
    }

    public DataInconsistencyException(Throwable cause)
    {
        super(cause);
    }

    public DataInconsistencyException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
