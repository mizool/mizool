package com.github.mizool.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.experimental.UtilityClass;

import com.google.common.util.concurrent.UncheckedExecutionException;

@UtilityClass
public class Futures
{
    public <V> V get(Future<V> future)
    {
        try
        {
            return future.get();
        }
        catch (@SuppressWarnings("squid:S2142") InterruptedException e)
        {
            Threads.rethrowInterrupt(e);
            return null; //this will never happen as rethrowInterrupt always throws an unchecked exception
        }
        catch (ExecutionException e)
        {
            throw new UncheckedExecutionException(e);
        }
    }
}