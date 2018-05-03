package com.github.mizool.core;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.UncheckedInterruptedException;

@UtilityClass
public class Threads
{
    public void sleep(int milliSeconds)
    {
        try
        {
            Thread.sleep(milliSeconds);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new UncheckedInterruptedException(e);
        }
    }
}
