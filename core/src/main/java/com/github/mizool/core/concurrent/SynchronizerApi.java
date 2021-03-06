/*
 * Copyright 2020-2021 incub8 Software Labs GmbH
 * Copyright 2020-2021 protel Hotelsoftware GmbH
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

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Provides fluent syntax for for {@link Synchronizer}.
 */
public interface SynchronizerApi
{
    /*
     * Note on terminology: this API intentionally uses the verbs "sleep" and "wake". If it used "wait" and "notify"
     * instead, its methods would all too easily be confused with methods in java.lang.Object which, if invoked on the
     * objects returned by chained methods, could cause deadlocks.
     */

    interface SleepRunGet extends RunGet
    {
        /**
         * Adds sleeping to the action chain. <br>
         * <br>
         * When performing this action, the chain will sleep until another chain wakes it up, then call the given
         * supplier. If the supplier returns {@code false}, this action chain resumes sleeping. Otherwise, the chain
         * continues by performing the main action.
         *
         * @param state the supplier that returns {@code true} if the action chain should continue, {@code false}
         * otherwise.
         *
         * @throws NullPointerException if {@code state} is null
         */
        RunGet sleepUntil(BooleanSupplier state);
    }

    interface RunGet
    {
        /**
         * Sets the given {@link Runnable} as the main action of the chain.
         *
         * @param runnable the action to perform
         *
         * @throws NullPointerException if {@code runnable} is null
         */
        Run.WakeSleepInvoke run(Runnable runnable);

        /**
         * Sets the given {@link Supplier} as the main action of the chain.
         *
         * @param getter the action to perform
         *
         * @throws NullPointerException if {@code getter} is null
         */
        <T> Get.WakeSleepInvoke<T> get(Supplier<T> getter);
    }

    interface Run
    {
        interface WakeSleepInvoke extends SleepInvoke, Base.WakeSleepInvoke
        {
            @Override
            SleepInvoke andWakeOthers();
        }

        interface SleepInvoke extends Invoke, Base.SleepInvoke
        {
            @Override
            Invoke thenSleepUntil(BooleanSupplier state);
        }

        interface Invoke
        {
            /**
             * Invokes the action chain in a synchronized block. <br>
             * <br>
             * All actions will be performed in order. This method blocks until all actions in the chain have been
             * completed.<br>
             * <br>
             * While several action chains can be invoked concurrently on the same synchronizer, only one of them will
             * perform an action at any given time. As this involves acquiring a lock shared with other action chains,
             * care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks and
             * {@link Object#wait()} / {@link Object#notifyAll()} directly.
             *
             * @throws com.github.mizool.core.exception.UncheckedInterruptedException if the thread was interrupted
             * while waiting (i.e. performing a sleep action).
             */
            void invoke();
        }
    }

    interface Get
    {
        interface WakeSleepInvoke<T> extends SleepInvoke<T>, Base.WakeSleepInvoke
        {
            @Override
            SleepInvoke<T> andWakeOthers();

            /**
             * Adds an action which conditionally wakes other chains.
             *
             * @param predicate a test for the result of the main action that returns {@code true} if this action chain
             * should wake other chains, {@code false} otherwise.
             *
             * @throws NullPointerException if {@code predicate} is null
             */
            SleepInvoke<T> andWakeOthersIf(Predicate<T> predicate);
        }

        interface SleepInvoke<T> extends Invoke<T>, Base.SleepInvoke
        {
            @Override
            Invoke<T> thenSleepUntil(BooleanSupplier state);
        }

        interface Invoke<T>
        {
            /**
             * Invokes the action chain in a synchronized block. <br>
             * <br>
             * All actions will be performed in order. This method blocks until all actions in the chain have been
             * completed.<br>
             * <br>
             * While several action chains can be invoked concurrently on the same synchronizer, only one of them will
             * perform an action at any given time. As this involves acquiring a lock shared with other action chains,
             * care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks and
             * {@link Object#wait()} / {@link Object#notifyAll()} directly.
             *
             * @return the object returned by the main action
             *
             * @throws com.github.mizool.core.exception.UncheckedInterruptedException if the thread was interrupted
             * while waiting (i.e. performing a sleep action).
             */
            T invoke();
        }
    }

    interface Base
    {
        interface WakeSleepInvoke extends SleepInvoke
        {
            /**
             * Adds an action which wakes other chains.
             */
            Object andWakeOthers();
        }

        interface SleepInvoke
        {
            /**
             * Adds sleeping to the end of the action chain. <br>
             * <br>
             * When performing this action, the chain will sleep until another chain wakes it up, then call the given
             * supplier. If the supplier returns {@code false}, this action chain resumes sleeping. Otherwise, the chain
             * invocation finishes.
             *
             * @param state the supplier that returns {@code true} if the chain invocation should finish, {@code false}
             * otherwise.
             *
             * @throws NullPointerException if {@code state} is null
             */
            Object thenSleepUntil(BooleanSupplier state);
        }
    }
}
