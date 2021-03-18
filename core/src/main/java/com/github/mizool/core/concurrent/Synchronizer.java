package com.github.mizool.core.concurrent;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;

import com.github.mizool.core.exception.UncheckedInterruptedException;

/**
 * TODO write docs.
 *
 * <ul>
 *     <li>define a sequence of actions, all executed inside the same synchronized block</li>
 *     <li>sleep until <i>condition</i><ul>
 *         <li>before/after main action (or both)</li>
 *         <li>evaluated inside the synchronized block when waking; false => sleep again</li>
 *         <li>throws {@link UncheckedInterruptedException}</li>
 *     </ul></li>
 *     <li>"wake others" = {@link Object#notifyAll()}</li>
 * </ul>
 *
 * <img src="doc-files/synchronizer-railroad-diagram.svg">
 *
 * <pre>{@code
 * synchronizer.run(...)
 *     .thenSleepUntil(...)
 *     .invoke();
 *
 * synchronizer.sleepUntil(...)
 *     .run(...)
 *     .andWakeOthers()
 *     .invoke();
 *
 * ResultClass result = synchronizer.get(...)
 *     .invoke();
 *
 * Spline result = synchronizer.get(...)
 *     .andWakeOthersIf(Spline::isReticulated)
 *     .invoke();}</pre>
 */
public final class Synchronizer implements SynchronizerDsl.Pre
{
    @With
    @Builder
    private static final class RunAction implements SynchronizerDsl.RunPostMayWake
    {
        @NonNull
        private final Lock lock;

        private final BooleanSupplier preState;

        @NonNull
        private final Runnable runnable;

        private final BooleanSupplier postState;

        private final boolean wakeOthers;

        @Override
        public void invoke()
        {
            synchronized (lock)
            {
                lock.sleepUntil(preState);

                runnable.run();

                if (wakeOthers)
                {
                    lock.notifyAll();
                }

                lock.sleepUntil(postState);
            }
        }

        @Override
        public SynchronizerDsl.RunPostMaySleep andWakeOthers()
        {
            return withWakeOthers(true);
        }

        @Override
        public SynchronizerDsl.RunInvokable thenSleepUntil(BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    @With
    @Builder
    private static final class GetAction<T> implements SynchronizerDsl.GetPostMayWake<T>
    {
        @NonNull
        private final Lock lock;

        private final BooleanSupplier preState;

        @NonNull
        private final Supplier<T> getter;

        private final Predicate<T> wakeOthersPredicate;

        private final BooleanSupplier postState;

        @Override
        public T invoke()
        {
            synchronized (lock)
            {
                lock.sleepUntil(preState);

                T result = getter.get();

                if (wakeOthersPredicate != null && wakeOthersPredicate.test(result))
                {
                    lock.notifyAll();
                }

                lock.sleepUntil(postState);

                return result;
            }
        }

        @Override
        public SynchronizerDsl.GetPostMaySleep<T> andWakeOthers()
        {
            return andWakeOthersIf(t -> true);
        }

        @Override
        public SynchronizerDsl.GetPostMaySleep<T> andWakeOthersIf(Predicate<T> predicate)
        {
            return withWakeOthersPredicate(predicate);
        }

        @Override
        public SynchronizerDsl.GetInvokable<T> thenSleepUntil(BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    @Builder
    private static class ActionChoice implements SynchronizerDsl.Action
    {
        @NonNull
        protected final Lock lock;

        private final BooleanSupplier preState;

        @Override
        public SynchronizerDsl.RunPostMayWake run(Runnable runnable)
        {
            return RunAction.builder()
                .lock(lock)
                .preState(preState)
                .runnable(runnable)
                .build();
        }

        @Override
        public <T> SynchronizerDsl.GetPostMayWake<T> get(Supplier<T> getter)
        {
            return GetAction.<T>builder().lock(lock)
                .preState(preState)
                .getter(getter)
                .build();
        }
    }

    @RequiredArgsConstructor
    private static class Lock
    {
        protected void sleepUntil(BooleanSupplier state)
        {
            if (state == null)
            {
                return;
            }

            // The caller should already hold this lock, so this is a no-op. We keep it to make code analyzers happy.
            synchronized (this)
            {
                try
                {
                    while (!state.getAsBoolean())
                    {
                        wait();
                    }
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread()
                        .interrupt();
                    throw new UncheckedInterruptedException(e);
                }
            }
        }
    }

    private final Lock lock = new Lock();

    @Override
    public SynchronizerDsl.RunPostMayWake run(Runnable runnable)
    {
        return actionChoiceBuilder().build()
            .run(runnable);
    }

    @Override
    public <T> SynchronizerDsl.GetPostMayWake<T> get(Supplier<T> getter)
    {
        return actionChoiceBuilder().build()
            .get(getter);
    }

    @Override
    public SynchronizerDsl.Action sleepUntil(BooleanSupplier state)
    {
        return actionChoiceBuilder().preState(state)
            .build();
    }

    private ActionChoice.ActionChoiceBuilder actionChoiceBuilder()
    {
        return ActionChoice.builder()
            .lock(lock);
    }
}
