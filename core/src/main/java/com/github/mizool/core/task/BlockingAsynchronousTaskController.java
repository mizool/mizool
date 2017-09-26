/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.core.task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Runs asynchronous tasks that supply {@link com.google.common.util.concurrent.ListenableFuture}s. The task execution
 * of a new task is delayed if the {@code taskLimit}, specified during construction, is reached. Delayed tasks are
 * resumed when running tasks complete by invoking the callback on the {@link com.google.common.util.concurrent.ListenableFuture}.
 */
// we decided not to test this class as writing a correct multi threaded test seems to be quite hard
@RequiredArgsConstructor
public class BlockingAsynchronousTaskController
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

    private final int taskLimit;

    private final AtomicInteger runningTasks = new AtomicInteger();
    private final Object semaphore = new Object();
    private final CompletionListener completionListener = new CompletionListener();

    /**
     * Runs the given task. The task execution is delayed if the {@code taskLimit}, specified during construction, is
     * reached.
     *
     * @param task A task that returns a {@link com.google.common.util.concurrent.ListenableFuture}.
     */
    public void beginTask(Supplier<ListenableFuture<?>> task)
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

            ListenableFuture<?> future = task.get();

            future.addListener(completionListener, MoreExecutors.directExecutor());
        }
    }
}