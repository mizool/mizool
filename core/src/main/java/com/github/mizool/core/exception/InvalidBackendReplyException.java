package com.github.mizool.core.exception;

/**
 * Thrown when an invalid reply was received from a backend system.
 */
public class InvalidBackendReplyException extends RuntimeException
{
    public InvalidBackendReplyException()
    {
        super();
    }

    public InvalidBackendReplyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidBackendReplyException(String message)
    {
        super(message);
    }

    public InvalidBackendReplyException(Throwable cause)
    {
        super(cause);
    }
}
