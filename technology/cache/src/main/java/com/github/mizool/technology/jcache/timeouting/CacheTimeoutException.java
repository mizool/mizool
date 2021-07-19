package com.github.mizool.technology.jcache.timeouting;

public class CacheTimeoutException extends RuntimeException
{
    public CacheTimeoutException()
    {
    }

    public CacheTimeoutException(String message)
    {
        super(message);
    }

    public CacheTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CacheTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
