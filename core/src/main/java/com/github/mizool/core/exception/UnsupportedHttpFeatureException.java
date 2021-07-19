package com.github.mizool.core.exception;

public class UnsupportedHttpFeatureException extends RuntimeException
{
    public UnsupportedHttpFeatureException()
    {
        super();
    }

    public UnsupportedHttpFeatureException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnsupportedHttpFeatureException(String message)
    {
        super(message);
    }

    public UnsupportedHttpFeatureException(Throwable cause)
    {
        super(cause);
    }
}
