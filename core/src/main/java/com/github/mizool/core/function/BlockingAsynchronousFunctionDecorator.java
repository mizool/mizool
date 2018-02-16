/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.function;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Decorates {@link java.util.function.Function}s that return
 * {@link com.google.common.util.concurrent.ListenableFuture}s. The application of the
 * {@link java.util.function.Function} is delayed if the {@code taskLimit}, specified during construction, is reached.
 * Delayed application is resumed when running {@link java.util.function.Function}s complete by invoking the callback on
 * the {@link com.google.common.util.concurrent.ListenableFuture}.
 *
 * @param <R> The type of the {@link com.google.common.util.concurrent.ListenableFuture} returned by the function.
 */
// we decided not to test this class as writing a correct multi threaded test seems to be quite hard
@RequiredArgsConstructor
public class BlockingAsynchronousFunctionDecorator<T, R> implements Function<T, ListenableFuture<R>>
{
    private class CompletionListener implements Runnable
    {

        @Override
        public void run()
        {
            synchronized (semaphore)
            {
                runningTasks.decrementAndGet();
                semaphore.notifyAll();
            }
        }

    }

    private final Function<T, ListenableFuture<R>> target;
    private final int taskLimit;

    private final AtomicInteger runningTasks = new AtomicInteger();
    private final Object semaphore = new Object();
    private final CompletionListener completionListener = new CompletionListener();

    /**
     * Applies the {@code target} {@link java.util.function.Function} to the given argument. Application is delayed if
     * the {@code taskLimit}, is reached.
     *
     * @param t The argument passed to the {@link java.util.function.Function}.
     *
     * @return The {@link com.google.common.util.concurrent.ListenableFuture} returned by the {@code target}
     * {@link java.util.function.Function}.
     */
    @Override
    public ListenableFuture<R> apply(T t)
    {
        synchronized (semaphore)
        {
            while (runningTasks.intValue() >= taskLimit)
            {
                try
                {
                    semaphore.wait();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
            runningTasks.incrementAndGet();

            ListenableFuture<R> future = target.apply(t);
            future.addListener(completionListener, MoreExecutors.directExecutor());
            return future;
        }
    }
}