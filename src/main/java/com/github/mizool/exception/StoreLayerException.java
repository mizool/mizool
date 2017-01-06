package com.github.mizool.exception;

public class StoreLayerException extends RuntimeException
{
    public StoreLayerException()
    {
    }

    public StoreLayerException(String message)
    {
        super(message);
    }

    public StoreLayerException(Throwable cause)
    {
        super(cause);
    }

    public StoreLayerException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
