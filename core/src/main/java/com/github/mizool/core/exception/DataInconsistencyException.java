package com.github.mizool.core.exception;

/**
 * Thrown when the persistent application data is in an invalid and/or inconsistent state and cannot be processed.<br>
 * <br>
 * If the error lies within the <i>configuration</i> instead of the application data, use {@link ConfigurationException}
 * instead.
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
