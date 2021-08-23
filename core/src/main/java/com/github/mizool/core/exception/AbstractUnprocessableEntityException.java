package com.github.mizool.core.exception;

/**
 * Thrown when the entity is syntactically correct (e.g. all non-null properties are set), but could not be processed
 * for semantic reasons (e.g. invalid references to other database entities).
 * Extend this class to create an exception class specific to the respective problem.
 */
public abstract class AbstractUnprocessableEntityException extends RuntimeException
{
    public AbstractUnprocessableEntityException()
    {
        super();
    }

    public AbstractUnprocessableEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public AbstractUnprocessableEntityException(String message)
    {
        super(message);
    }

    public AbstractUnprocessableEntityException(Throwable cause)
    {
        super(cause);
    }
}
