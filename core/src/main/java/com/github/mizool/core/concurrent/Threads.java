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
            rethrowInterrupt(e);
        }
    }

    /**
     * Wraps the given {@link InterruptedException} in an {@link UncheckedInterruptedException} and re-interrupts the
     * thread.
     *
     * @throws UncheckedInterruptedException Wrapping the given {@link InterruptedException}.
     */
    public void rethrowInterrupt(InterruptedException e)
    {
        Thread.currentThread()
            .interrupt();
        throw new UncheckedInterruptedException(e);
    }
}
