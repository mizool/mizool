package com.github.mizool.core.exception;

/**
 * Thrown when an attempt is made to change the value of a field that is considered read-only for the current operation.
 * For example, it may be forbidden to change the name of a user group, although it must be set when creating one.
 */
public class ReadonlyFieldException extends AbstractUnprocessableEntityException
{
    public ReadonlyFieldException()
    {
        super();
    }

    public ReadonlyFieldException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ReadonlyFieldException(String message)
    {
        super(message);
    }

    public ReadonlyFieldException(Throwable cause)
    {
        super(cause);
    }
}
