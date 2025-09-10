package com.github.mizool.core.concurrent;

import java.time.Duration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.UncheckedTimeoutException;

@UtilityClass
public class Futures
{
    private static class ResultVoidingListenableFuture implements ListenableFuture<Void>
    {
        private class Callback<V> implements FutureCallback<V>
        {
            @Override
            public void onSuccess(V result)
            {
                target.set(null);
            }

            @Override
            public void onFailure(Throwable t)
            {
                target.setException(t);
            }
        }

        private final SettableFuture<Void> target = SettableFuture.create();

        public <T> ResultVoidingListenableFuture(ListenableFuture<T> target)
        {
            com.google.common.util.concurrent.Futures.addCallback(target,
                new Callback<>(),
                MoreExecutors.directExecutor());
        }

        /*
         * We don't use lombok for delegation as
         * a) somehow, lombok 1.16.6 does not correctly generate delegate methods for the methods not directly defined in
         * SettableFuture.
         * b) we want to implement ListenableFuture, not be a SettableFuture.
         */

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException
        {
            return target.get(timeout, unit);
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException
        {
            return target.get();
        }

        @Override
        public boolean isDone()
        {
            return target.isDone();
        }

        @Override
        public boolean isCancelled()
        {
            return target.isCancelled();
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning)
        {
            return target.cancel(mayInterruptIfRunning);
        }

        @Override
        public void addListener(Runnable listener, Executor exec)
        {
            target.addListener(listener, exec);
        }
    }

    private interface Getter<V>
    {
        V call() throws ExecutionException, InterruptedException, TimeoutException;
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     *
     * @throws UncheckedExecutionException if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     * @throws UncheckedInterruptedException if the thread was interrupted while waiting. Note: <a href="http://www.yegor256.com/2015/10/20/interrupted-exception.html">as required</a>, the thread will be re-interrupted before throwing the exception.
     * @throws NullPointerException if {@code future} is {@code null}
     */
    public <V> V get(@NonNull Future<V> future)
    {
        return get(future::get);
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available.
     *
     * @return the computed result
     *
     * @throws UncheckedTimeoutException if the computation timed out
     * @throws UncheckedExecutionException if the computation threw an exception
     * @throws CancellationException if the computation was cancelled
     * @throws UncheckedInterruptedException if the thread was interrupted while waiting. Note: <a href="http://www.yegor256.com/2015/10/20/interrupted-exception.html">as required</a>, the thread will be re-interrupted before throwing the exception.
     * @throws NullPointerException if {@code future} or {@code duration} is {@code null}
     */
    public <V> V get(@NonNull Future<V> future, @NonNull Duration duration)
    {
        return get(() -> future.get(duration.toNanos(), TimeUnit.NANOSECONDS));
    }

    private <V> V get(Getter<V> getter)
    {
        try
        {
            return getter.call();
        }
        catch (@SuppressWarnings("squid:S2142") InterruptedException e)
        {
            Thread.currentThread()
                .interrupt();
            throw new UncheckedInterruptedException(e);
        }
        catch (ExecutionException e)
        {
            throw new UncheckedExecutionException(e);
        }
        catch (TimeoutException e)
        {
            throw new UncheckedTimeoutException(e);
        }
    }

    /**
     * Transforms any {@link ListenableFuture} into a {@link ListenableFuture} without result. Exceptions that are
     * thrown by the original future are handled transparently.
     *
     * <p>The returned future does not contain a reference to the original future. That allows to reduce the overall
     * memory footprint when working with multiple futures at once without caring for the results.
     *
     * @param future the future to wrap
     *
     * @return a void future
     *
     * @throws NullPointerException if {@code future} is {@code null}
     */
    public ListenableFuture<Void> toVoidResult(@NonNull ListenableFuture<?> future)
    {
        return new ResultVoidingListenableFuture(future);
    }

    /**
     * Transforms any {@link CompletableFuture} into a {@link CompletableFuture} without result. Exceptions that are
     * thrown by the original future are handled transparently.
     *
     * <p>The returned future does not contain a reference to the original future. That allows to reduce the overall
     * memory footprint when working with multiple futures at once without caring for the results.
     *
     * @param future the future to wrap
     *
     * @return a void future
     *
     * @throws NullPointerException if {@code future} is {@code null}
     */
    public CompletableFuture<Void> toVoidResult(@NonNull CompletableFuture<?> future)
    {
        return future.thenAccept(o -> {
        });
    }
}
