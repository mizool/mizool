package com.github.mizool.core.exception;

/**
 * Thrown to indicate that a method was not yet implemented by the developer. This is intended to be temporary and will
 * cause a deprecation warning during compilation. Thus, if there are no concrete plans to implement the method soon,
 * you should use the JDK {@link UnsupportedOperationException} instead (e.g. as with unmodifiable lists).
 */
public class NotYetImplementedException extends CodeInconsistencyException
{
    /**
     * @deprecated Deprecated to remind you to implement the corresponding code before releasing the software.
     */
    @Deprecated(since = "0.1")
    public NotYetImplementedException()
    {
    }

    /**
     * @deprecated Deprecated to remind you to implement the corresponding code before releasing the software.
     */
    @Deprecated(since = "0.1")
    public NotYetImplementedException(String message)
    {
        super(message);
    }
}
