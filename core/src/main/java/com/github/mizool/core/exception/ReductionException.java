package com.github.mizool.core.exception;

/**
 * Thrown when there is an error during a {@link java.util.stream.Stream} reduction operation.
 */
public class ReductionException extends RuntimeException
{
    public ReductionException()
    {
        super();
    }

    public ReductionException(String message)
    {
        super(message);
    }

    public ReductionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ReductionException(Throwable cause)
    {
        super(cause);
    }
}
