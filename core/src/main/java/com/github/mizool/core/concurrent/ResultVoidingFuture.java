/*
 * Copyright 2018-2020 incub8 Software Labs GmbH
 * Copyright 2018-2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Transforms any {@link com.google.common.util.concurrent.ListenableFuture} into a {@link
 * com.google.common.util.concurrent.ListenableFuture} without result. Exceptions that are thrown by the original future
 * are handled transparently.<br>
 * <br>
 * Note that the reference to the original future is discarded as soon as possible. That allows to reduce the overall
 * memory footprint when working with multiple futures at once without caring for the results.
 *
 * @deprecated Use the factory method {@link com.github.mizool.core.concurrent.Futures#toVoidResult(ListenableFuture)}
 *     instead. This class will cease to be public at some point.
 */
@Deprecated
public class ResultVoidingFuture implements ListenableFuture<Void>
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

    /**
     * @deprecated Use the factory method
     * {@link com.github.mizool.core.concurrent.Futures#toVoidResult(ListenableFuture)}
     *     instead. This class will cease to be public at some point.
     */
    @Deprecated
    public <T> ResultVoidingFuture(ListenableFuture<T> target)
    {
        Futures.addCallback(target, new Callback<>(), MoreExecutors.directExecutor());
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