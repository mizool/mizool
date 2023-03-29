package com.github.mizool.core.concurrent;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;

/**
 * Provides fluent syntax for {@link Synchronizer}.
 */
public interface SynchronizerApi
{
    @CheckReturnValue
    interface Fluent
    {
    }

    interface SleepRunGet extends Fluent, RunGet
    {
        /**
         * Adds sleeping to the action chain. <br>
         * <br>
         * When performing this action, the chain will ensure the given condition is met before performing the next
         * action. If the supplier returns {@code false}, this action chain sleeps until another chain wakes it up.
         * Then, the supplier is called again and if it still returns {@code false}, the chain resumes sleeping.
         * Otherwise, the chain proceeds to perform the next action.
         *
         * @param state the supplier that returns {@code true} if the action chain should continue, {@code false}
         * otherwise.
         *
         * @throws NullPointerException if {@code state} is null
         */
        default RunGetInvoke sleepUntil(BooleanSupplier state)
        {
            return sleepUntil(state, null);
        }

        /**
         * Adds sleeping to the action chain. <br>
         * <br>
         * When performing this action, the chain will ensure the given condition is met before performing the next
         * action. If the supplier returns {@code false}, this action chain sleeps until either another chain wakes it
         * up or the timeout has passed. Then, the supplier is called again and if it still returns {@code false}, the
         * chain resumes sleeping. Otherwise, the chain proceeds to perform the next action.
         *
         * @param state the supplier that returns {@code true} if the action chain should continue, {@code false}
         * otherwise.
         * @param timeout the maximum amount of time to sleep before re-checking the condition, or {@code null} to
         * sleep indefinitely.
         *
         * @throws NullPointerException if {@code state} is null
         */
        RunGetInvoke sleepUntil(BooleanSupplier state, Duration timeout);

        /**
         * Adds an action which wakes other chains.
         */
        default Run.Invoke wakeOthers()
        {
            return run(() -> {
            }).andWakeOthers();
        }
    }

    interface RunGet extends Fluent
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

    interface RunGetInvoke extends Fluent, RunGet, Run.Invoke
    {
        @Override
        default void invoke()
        {
            Runnable noOp = () -> {
            };

            run(noOp).invoke();
        }
    }

    interface Run
    {
        interface WakeSleepInvoke extends Fluent, SleepInvoke, Base.WakeSleepInvoke
        {
            @Override
            SleepInvoke andWakeOthers();
        }

        interface SleepInvoke extends Fluent, Invoke, Base.SleepInvoke
        {
            @Override
            default Invoke thenSleepUntil(BooleanSupplier state)
            {
                return thenSleepUntil(state, null);
            }

            @Override
            Invoke thenSleepUntil(BooleanSupplier state, Duration timeout);
        }

        interface Invoke extends Fluent
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
        interface WakeSleepInvoke<T> extends Fluent, SleepInvoke<T>, Base.WakeSleepInvoke
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

        interface SleepInvoke<T> extends Fluent, Invoke<T>, Base.SleepInvoke
        {
            @Override
            default Invoke<T> thenSleepUntil(BooleanSupplier state)
            {
                return thenSleepUntil(state, null);
            }

            @Override
            Invoke<T> thenSleepUntil(BooleanSupplier state, Duration timeout);
        }

        interface Invoke<T> extends Fluent
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
            @CanIgnoreReturnValue
            T invoke();
        }
    }

    interface Base
    {
        interface WakeSleepInvoke extends Fluent, SleepInvoke
        {
            /**
             * Adds an action which wakes other chains.
             */
            Object andWakeOthers();
        }

        interface SleepInvoke extends Fluent
        {
            /**
             * Adds sleeping to the action chain. <br>
             * <br>
             * When performing this action, the chain will ensure the given condition is met before returning. If the
             * supplier returns {@code false}, this action chain sleeps until another chain wakes it up. Then, the
             * supplier is called again and if it still returns {@code false}, the chain resumes sleeping. Otherwise,
             * the chain invocation finishes.
             *
             * @param state the supplier that returns {@code true} if the action chain should finish, {@code false}
             * otherwise.
             *
             * @throws NullPointerException if {@code state} is null
             */
            default Object thenSleepUntil(BooleanSupplier state)
            {
                return thenSleepUntil(state, null);
            }

            /**
             * Adds sleeping to the action chain. <br>
             * <br>
             * When performing this action, the chain will ensure the given condition is met before returning. If the
             * supplier returns {@code false}, this action chain sleeps until either another chain wakes it up or the
             * timeout has passed. Then, the supplier is called again and if it still returns {@code false}, the chain
             * resumes sleeping. Otherwise, the chain invocation finishes.
             *
             * @param state the supplier that returns {@code true} if the action chain should finish, {@code false}
             * otherwise.
             * @param timeout the maximum amount of time to sleep before re-checking the condition, or {@code null} to
             * sleep indefinitely.
             *
             * @throws NullPointerException if {@code state} is null
             */
            Object thenSleepUntil(BooleanSupplier state, Duration timeout);
        }
    }
}
