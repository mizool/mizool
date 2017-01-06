package com.github.mizool.exception;

/**
 * Represents problems that arise purely from inconsistencies in the source code, as opposed to problems that are caused
 * by user or environment data.<br>
 * <br>
 * For example, this exception may be used to wrap an "impossible" {@link java.io.UnsupportedEncodingException} for the
 * encoding "UTF-8" (which is guaranteed to be supported by the Java specs). If it is thrown, a developer might have
 * accidently changed the encoding (= a code inconsistency).
 */
public class CodeInconsistencyException extends RuntimeException
{
    public CodeInconsistencyException()
    {
    }

    public CodeInconsistencyException(String message)
    {
        super(message);
    }

    public CodeInconsistencyException(Throwable cause)
    {
        super(cause);
    }

    public CodeInconsistencyException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
