package com.github.mizool.exception;

public class PermissionDeniedException extends RuntimeException
{
    public PermissionDeniedException()
    {
    }

    public PermissionDeniedException(String message)
    {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PermissionDeniedException(Throwable cause)
    {
        super(cause);
    }

    public PermissionDeniedException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
