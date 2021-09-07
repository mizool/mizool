package com.github.mizool.technology.cassandra;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.github.mizool.core.concurrent.ResultVoidingFuture;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.exception.StoreLayerException;
import com.google.common.util.concurrent.ListenableFuture;

public class StoreLayerFuture implements ListenableFuture<Void>
{
    private final ListenableFuture<Void> target;

    public StoreLayerFuture(ListenableFuture<?> target)
    {
        this.target = new ResultVoidingFuture(target);
    }

    @Override
    public void addListener(Runnable listener, Executor executor)
    {
        target.addListener(listener, executor);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return target.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled()
    {
        return target.isCancelled();
    }

    @Override
    public boolean isDone()
    {
        return target.isDone();
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException
    {
        try
        {
            return target.get();
        }
        catch (QueryValidationException e)
        {
            throw new CodeInconsistencyException(e);
        }
        catch (QueryExecutionException | NoHostAvailableException e)
        {
            throw new StoreLayerException(e);
        }
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        try
        {
            return target.get(timeout, unit);
        }
        catch (QueryValidationException e)
        {
            throw new CodeInconsistencyException(e);
        }
        catch (QueryExecutionException | NoHostAvailableException e)
        {
            throw new StoreLayerException(e);
        }
    }
}
