package com.github.mizool.exception;

public class MethodNotAllowedException extends RuntimeException
{
    public MethodNotAllowedException()
    {
        super();
    }

    public MethodNotAllowedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MethodNotAllowedException(String message)
    {
        super(message);
    }

    public MethodNotAllowedException(Throwable cause)
    {
        super(cause);
    }
}