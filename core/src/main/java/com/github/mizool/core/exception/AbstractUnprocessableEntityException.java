package com.github.mizool.core.exception;

/**
 * Thrown when the entity is syntactically correct (e.g. all non-null properties are set), but could not be processed
 * for semantic reasons (e.g. invalid references to other database entities).
 * Extend this class to create an exception class specific to the respective problem.
 */
public abstract class AbstractUnprocessableEntityException extends RuntimeException
{
    protected AbstractUnprocessableEntityException()
    {
        super();
    }

    protected AbstractUnprocessableEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    protected AbstractUnprocessableEntityException(String message)
    {
        super(message);
    }

    protected AbstractUnprocessableEntityException(Throwable cause)
    {
        super(cause);
    }
}
