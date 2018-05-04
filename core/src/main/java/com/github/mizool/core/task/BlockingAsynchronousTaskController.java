/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.task;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

import com.github.mizool.core.concurrent.ListenableFutureCollector;
import com.github.mizool.core.concurrent.Threads;
import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Runs asynchronous tasks that supply {@link com.google.common.util.concurrent.ListenableFuture}s. The task execution
 * of a new task is delayed if the {@code taskLimit}, specified during construction, is reached. Delayed tasks are
 * resumed when running tasks complete by invoking the callback on the {@link com.google.common.util.concurrent.ListenableFuture}.
 *
 * @deprecated Use {@link ListenableFutureCollector} instead. The use of the
 * {@link BlockingAsynchronousTaskController} encourages splitting of {@link java.util.stream.Stream}s by accumulating
 * an {@link Iterable} of {@link com.google.common.util.concurrent.ListenableFuture}s inside the {@code task}
 * {@link Supplier}. Not only is that cumbersome to read but it is also not idiomatic for {@link java.util.stream.Stream}s and
 * bears the risk of accumulating a large number of {@link java.util.concurrent.Future}s before finally waiting for
 * them.
 */
// we decided not to test this class as writing a correct multi threaded test seems to be quite hard
@Deprecated
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
                Threads.wait(semaphore);
            }
            runningTasks.incrementAndGet();

            ListenableFuture<?> future = task.get();

            future.addListener(completionListener, MoreExecutors.directExecutor());
        }
    }
}