package com.github.mizool.core.exception;

/**
 * Represents problems with the system or application configuration that arise from incomplete or wrong installation.
 * Example: required entries in the database are missing.
 */
public class ConfigurationException extends RuntimeException
{
    public ConfigurationException()
    {
    }

    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
