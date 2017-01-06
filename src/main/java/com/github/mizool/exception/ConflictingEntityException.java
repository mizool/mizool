package com.github.mizool.exception;

/**
 * Thrown when the entity cannot be saved due to a conflict with the corresponding object in the database, e.g.
 * because the object that should be created already exists, or the object to be updated has conflicting changes.
 */
public class ConflictingEntityException extends RuntimeException
{
    public ConflictingEntityException()
    {
        super();
    }

    public ConflictingEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConflictingEntityException(String message)
    {
        super(message);
    }

    public ConflictingEntityException(Throwable cause)
    {
        super(cause);
    }
}