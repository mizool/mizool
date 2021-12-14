package com.github.mizool.core.exception;

/**
 * Thrown when the primary key of the entity is invalid. This is used both for general rules (e.g. primary key cannot be
 * {@code null}) and situational checks (e.g. primary key cannot be changed as part of an update).<br>
 * <br>
 * If the primary key is already in use by another record, use {@link ConflictingEntityException} instead.
 */
public class InvalidPrimaryKeyException extends AbstractUnprocessableEntityException
{
    public InvalidPrimaryKeyException()
    {
        super();
    }

    public InvalidPrimaryKeyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidPrimaryKeyException(String message)
    {
        super(message);
    }

    public InvalidPrimaryKeyException(Throwable cause)
    {
        super(cause);
    }
}
