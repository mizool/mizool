package com.github.mizool.core.concurrent;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;

/**
 * Provides the syntax for {@link FluentSynchronizer}.
 */
@NullMarked
public interface FluentSynchronizerApi
{
    @CheckReturnValue
    interface Common
    {
        /**
         * Adds sleeping to the action chain.
         *
         * <p>This action ensures the given condition is met before performing the next action. When this action begins,
         * the supplier is called immediately.
         *
         * <dl>
         *     <dt>Supplier returns {@code false}:</dt>
         *     <dd>
         *         The chain sleeps until another chain wakes it up. The supplier is then called again and if it still
         *         returns {@code false}, the chain resumes sleeping.
         *     </dd>
         *     <dt>Supplier returns {@code true}:</dt>
         *     <dd>
         *         The chain proceeds to perform the next action.
         *     </dd>
         * </dl>
         *
         * @param state the supplier that returns {@code true} if the action chain should proceed to the next action,
         * {@code false} otherwise.
         *
         * @throws NullPointerException if {@code state} is null
         */
        Object sleepUntil(BooleanSupplier state);

        /**
         * Adds sleeping to the action chain.
         *
         * <p>This action ensures the given condition is met before performing the next action. When this action begins,
         * the supplier is called immediately.
         *
         * <dl>
         *     <dt>Supplier returns {@code false}:</dt>
         *     <dd>
         *         The chain sleeps until either another chain wakes it up or the {@code checkInterval} has passed.
         *         <p>In both cases, the supplier is then called again and if it still returns {@code false}, the chain
         *         resumes sleeping.
         *     </dd>
         *     <dt>Supplier returns {@code true}:</dt>
         *     <dd>
         *         The chain proceeds to perform the next action.
         *     </dd>
         * </dl>
         *
         * @param state the supplier that returns {@code true} if the action chain should proceed to the next action,
         * {@code false} otherwise.
         * @param checkInterval how often the condition should be re-checked even if no wake call happens, or
         * {@code null} for "never".
         *
         * @throws NullPointerException if {@code state} is null
         */
        Object sleepUntil(BooleanSupplier state, Duration checkInterval);

        /**
         * Adds an action which wakes other chains.
         */
        Object wakeOthers();
    }

    interface Void extends Common
    {
        /**
         * Adds an action to the chain that calls the given {@link Runnable}.
         *
         * @param runnable the runnable to call
         *
         * @throws NullPointerException if {@code runnable} is null
         */
        VoidInvoke run(Runnable runnable);

        /**
         * Adds an action to the chain that calls the given {@link Supplier}.
         *
         * <p>The value returned by the supplier is kept as the <b>chain result</b> and will be returned by the
         * {@linkplain Value#invoke() chain invocation}. Intermediate actions can be used to
         * {@linkplain Value#map(Function) transform} or {@linkplain Value#discardResult() discard} the <i>chain
         * result</i>.
         *
         * @param supplier the supplier to call
         *
         * @throws NullPointerException if {@code supplier} is null
         */
        <T extends @Nullable Object> Value<T> get(Supplier<T> supplier);

        @Override
        VoidInvoke sleepUntil(BooleanSupplier state);

        @Override
        VoidInvoke sleepUntil(BooleanSupplier state, Duration checkInterval);

        @Override
        VoidInvoke wakeOthers();
    }

    interface VoidInvoke extends Void
    {
        /**
         * Invokes the action chain in a synchronized block.
         *
         * <p>All actions will be performed in order. This method blocks until all actions in the chain have been
         * completed.
         *
         * <p>While several action chains can be invoked concurrently on the same synchronizer, only one of them will
         * perform an action at any given time. As this involves acquiring a lock shared with other action chains,
         * care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks and
         * {@link Object#wait()} / {@link Object#notifyAll()} directly.
         *
         * @throws com.github.mizool.core.exception.UncheckedInterruptedException if the thread was interrupted
         * while waiting (i.e. performing a sleep action).
         */
        void invoke();
    }

    interface Value<T extends @Nullable Object> extends Common
    {
        @Override
        Value<T> sleepUntil(BooleanSupplier state);

        @Override
        Value<T> sleepUntil(BooleanSupplier state, Duration checkInterval);

        @Override
        Value<T> wakeOthers();

        /**
         * Adds an action which conditionally wakes other chains. If the condition is {@code false}, the chain
         * immediately proceeds to perform the next action.
         *
         * @param predicate a test operating on the <i>{@linkplain Void#get(Supplier) chain result}</i> that returns
         * {@code true} if this action chain should wake other chains, {@code false} otherwise.
         *
         * @throws NullPointerException if {@code predicate} is null
         */
        Value<T> wakeOthersIf(Predicate<T> predicate);

        /**
         * Adds an action to the chain that processes or transforms the
         * <i>{@linkplain Void#get(Supplier) chain result}</i>.
         *
         * @param function the mapping function
         *
         * @throws NullPointerException if {@code function} is null
         */
        <R extends @Nullable Object> Value<R> map(Function<T, R> function);

        /**
         * Invokes the action chain in a synchronized block.
         *
         * <p>All actions will be performed in order. This method blocks until all actions in the chain have been
         * completed.
         *
         * <p>While several action chains can be invoked concurrently on the same synchronizer, only one of them will
         * perform an action at any given time. As this involves acquiring a lock shared with other action chains,
         * care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks and
         * {@link Object#wait()} / {@link Object#notifyAll()} directly.
         *
         * @return the <i>{@linkplain Void#get(Supplier) chain result}</i>
         *
         * @throws com.github.mizool.core.exception.UncheckedInterruptedException if the thread was interrupted
         * while waiting (i.e. performing a sleep action).
         */
        @CanIgnoreReturnValue
        T invoke();

        /**
         * Adds an action to the chain that will discard the <i>{@linkplain Void#get(Supplier) chain result}</i>.
         * This allows adding subsequent {@link Void#run(Runnable) run(Runnable)} actions or a different
         * {@link Void#get(Supplier) get(Supplier)} action.
         */
        VoidInvoke discardResult();
    }
}
