package com.github.mizool.core.exception;

/**
 * Thrown when an explicit null check fails for a method argument.<br>
 * <br>
 * Note: the exception is named "Argument..." instead of "Parameter..." for consistency with
 * {@link IllegalArgumentException}, which it extends.
 */
public class ArgumentNullException extends IllegalArgumentException
{
    public ArgumentNullException(String argumentName)
    {
        super("Argument '" + argumentName + "' must not be null");
    }
}
