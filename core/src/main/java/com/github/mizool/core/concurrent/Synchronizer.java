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
 * TODO write docs
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
 * synchronizer.buildSequenceOf()
 *     .run(...)
 *     .thenSleepUntil(...)
 *     .invokeSequence();
 *
 * synchronizer.buildSequenceOf()
 *     .sleepUntil(...)
 *     .run(...)
 *     .andWakeOthers()
 *     .invokeSequence();
 *
 * ResultClass result = synchronizer.buildSequenceOf()
 *     .get(...)
 *     .invokeSequence();
 *
 * Spline result = synchronizer.buildSequenceOf()
 *     .get(...)
 *     .andWakeOthersIf(Spline::isReticulated)
 *     .invokeSequence();}</pre>
 */
public final class Synchronizer
{
    public interface Definition
    {
        interface Pre extends Action
        {
            Action sleepUntil(BooleanSupplier state);
        }

        interface Action
        {
            RunPostMayWake run(Runnable runnable);

            <T> GetPostMayWake<T> get(Supplier<T> getter);
        }

        interface RunPostMayWake extends RunPostMaySleep
        {
            RunPostMaySleep andWakeOthers();
        }

        interface RunPostMaySleep extends RunInvokable
        {
            RunInvokable thenSleepUntil(BooleanSupplier state);
        }

        interface RunInvokable
        {
            void invokeSequence();
        }

        interface GetPostMayWake<T> extends GetPostMaySleep<T>
        {
            GetPostMaySleep<T> andWakeOthers();

            GetPostMaySleep<T> andWakeOthersIf(Predicate<T> predicate);
        }

        interface GetPostMaySleep<T> extends GetInvokable<T>
        {
            GetInvokable<T> thenSleepUntil(BooleanSupplier state);
        }

        interface GetInvokable<T>
        {
            T invokeSequence();
        }
    }

    private static final class PreChoice extends ActionChoice implements Definition.Pre
    {
        private PreChoice(@NonNull Synchronizer.Lock lock)
        {
            super(lock, null);
        }

        @Override
        public Definition.Action sleepUntil(BooleanSupplier state)
        {
            return ActionChoice.builder()
                .lock(lock)
                .preState(state)
                .build();
        }
    }

    @Builder
    private static class ActionChoice implements Definition.Action
    {
        @NonNull
        protected final Lock lock;

        private final BooleanSupplier preState;

        @Override
        public Definition.RunPostMayWake run(Runnable runnable)
        {
            return RunAction.builder()
                .lock(lock)
                .preState(preState)
                .runnable(runnable)
                .build();
        }

        @Override
        public <T> Definition.GetPostMayWake<T> get(Supplier<T> getter)
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

    @With
    @Builder
    private static final class RunAction implements Definition.RunPostMayWake
    {
        @NonNull
        private final Lock lock;

        private final BooleanSupplier preState;

        @NonNull
        private final Runnable runnable;

        private final BooleanSupplier postState;

        private final boolean wakeOthers;

        @Override
        public void invokeSequence()
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
        public Definition.RunPostMaySleep andWakeOthers()
        {
            return withWakeOthers(true);
        }

        @Override
        public Definition.RunInvokable thenSleepUntil(BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    @With
    @Builder
    private static final class GetAction<T> implements Definition.GetPostMayWake<T>
    {
        @NonNull
        private final Lock lock;

        private final BooleanSupplier preState;

        @NonNull
        private final Supplier<T> getter;

        private final Predicate<T> wakeOthersPredicate;

        private final BooleanSupplier postState;

        @Override
        public T invokeSequence()
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
        public Definition.GetPostMaySleep<T> andWakeOthers()
        {
            return andWakeOthersIf(t -> true);
        }

        @Override
        public Definition.GetPostMaySleep<T> andWakeOthersIf(Predicate<T> predicate)
        {
            return withWakeOthersPredicate(predicate);
        }

        @Override
        public Definition.GetInvokable<T> thenSleepUntil(BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    private final Lock lock = new Lock();

    public Definition.Pre buildSequenceOf()
    {
        return new PreChoice(lock);
    }
}
