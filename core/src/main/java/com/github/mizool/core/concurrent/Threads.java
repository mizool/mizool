package com.github.mizool.core.concurrent;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.UncheckedInterruptedException;

@UtilityClass
public class Threads
{
    /**
     * @throws UncheckedInterruptedException If the thread was interrupted.
     */
    public void sleep(long milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread()
                .interrupt();
            throw new UncheckedInterruptedException(e);
        }
    }

    /**
     * Wraps the given {@link InterruptedException} in an {@link UncheckedInterruptedException} and re-interrupts the
     * thread.
     *
     * @throws UncheckedInterruptedException Wrapping the given {@link InterruptedException}.
     * @deprecated at the usage site, the compiler/IDE can’t know that {@code rethrowInterrupt()} contains a {@code throw}, so it will complain that an exception was neither handled nor rethrown.
     */
    @Deprecated(forRemoval = true)
    public void rethrowInterrupt(InterruptedException e)
    {
        Thread.currentThread()
            .interrupt();
        throw new UncheckedInterruptedException(e);
    }
}
