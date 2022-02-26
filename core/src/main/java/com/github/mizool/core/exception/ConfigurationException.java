package com.github.mizool.core.exception;

/**
 * Represents problems with the system or application configuration that arise from incomplete or wrong installation.
 * Example: required settings in a properties file or configuration table are missing.<br>
 * <br>
 * If the error lies within the <i>application data</i> instead of the configuration, use
 * {@link DataInconsistencyException} instead.
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
