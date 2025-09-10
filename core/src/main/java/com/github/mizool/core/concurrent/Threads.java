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
}
