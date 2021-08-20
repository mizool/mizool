package com.github.mizool.core.exception;

/**
 * Wraps an {@link InterruptedException}.<br>
 * <br>
 * Remember to call {@code Thread.currentThread().interrupt();} before throwing this exception.<br>
 * See <a href="http://www.yegor256.com/2015/10/20/interrupted-exception.html">this article</a> for details.
 */
public class UncheckedInterruptedException extends RuntimeException
{
    public UncheckedInterruptedException(String message, InterruptedException cause)
    {
        super(message, cause);
    }

    public UncheckedInterruptedException(InterruptedException cause)
    {
        super(cause);
    }
}
