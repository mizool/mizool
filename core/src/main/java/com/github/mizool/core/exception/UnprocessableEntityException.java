package com.github.mizool.core.exception;

/**
 * Thrown when the entity is syntactically correct (e.g. all non-null properties are set), but could not be processed
 * for semantic reasons (e.g. invalid references to other database entities).
 */
public class UnprocessableEntityException extends RuntimeException
{
    public UnprocessableEntityException()
    {
        super();
    }

    public UnprocessableEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnprocessableEntityException(String message)
    {
        super(message);
    }

    public UnprocessableEntityException(Throwable cause)
    {
        super(cause);
    }
}
