package com.github.mizool.core.exception;

/**
 * Thrown when an attempt is made to set a value for a field which is automatically generated or maintained, such as the
 * identifier or timestamps.
 */
public class GeneratedFieldOverrideException extends AbstractUnprocessableEntityException
{
    public GeneratedFieldOverrideException()
    {
        super();
    }

    public GeneratedFieldOverrideException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public GeneratedFieldOverrideException(String message)
    {
        super(message);
    }

    public GeneratedFieldOverrideException(Throwable cause)
    {
        super(cause);
    }
}
