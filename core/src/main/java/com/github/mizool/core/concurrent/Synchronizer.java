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
 * Encapsulates synchronization and wait/notify functionality. <br>
 * <br>
 * <h3>Introduction</h3>
 * By calling any of the instance methods of {@code Synchronizer}, the calling code defines a chain of actions that is
 * completed with {@code invoke()}. All those actions are performed in one synchronized block using a lock which is
 * private to the {@code Synchronizer} instance.<br>
 * <br>
 * With its fluent API and encapsulated lock, this class can help increasing both the readability and robustness of
 * concurrent algorithms. Still, care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks
 * and {@link Object#wait()} / {@link Object#notifyAll()} directly.<br>
 * <br>
 * <h3>Actions</h3>
 * {@code Synchronizer} provides the following actions:<br>
 * <br>
 * <ul>
 *     <li>Main action
 *         <ul><li>
 *             a {@link Runnable} ({@code run}) or {@link Supplier} ({@code get})<br>
 *         </li></ul><br>
 *     </li>
 *     <li>Sleep until <i>condition</i> is {@code true}
 *         <ul><li>
 *             Can be used before ({@code sleepUntil}) and/or after ({@code thenSleepUntil}) the main action.<br>
 *             Sleeping is implemented in line with secure coding practices, i.e. {@link Object#wait()} is called inside
 *             a loop to ensure liveness and safety (see
 *             <a href="https://wiki.sei.cmu.edu/confluence/display/java/THI03-J.+Always+invoke+wait%28%29+and+await%28%29+methods+inside+a+loop">SEI
 *             CERT rule THI03-J</a> for details).<br>
 *             If the thread is interrupted while waiting, {@link UncheckedInterruptedException} is thrown and the
 *             thread's interrupted flag is re-set.<br>
 *         </li></ul><br>
 *     </li>
 *     <li>Wake other threads</li>
 *         <ul><li>
 *             Using {@code wakeOthers()} is equivalent to calling {@link Object#notifyAll()}. If the main action is a
 *     {@code Supplier<T>}, the calling code can decide whether waking takes place by using
 *     {@code andWakeOthersIf(Predicate<T>)} instead.<br>
 *         </li></ul><br>
 *     </li>
 * </ul>
 * <h3>Fluent API overview</h3>
 * <img src="doc-files/synchronizer-railroad-diagram.svg"><br>
 * <br>
 * <h3>Example usage</h3>
 * <pre>{@code synchronizer.run(...)
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
public final class Synchronizer implements SynchronizerApi.SleepRunGet
{
    @With
    @Builder
    private static final class RunAction implements SynchronizerApi.Run.WakeSleepInvoke
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
        public SynchronizerApi.Run.SleepInvoke andWakeOthers()
        {
            return withWakeOthers(true);
        }

        @Override
        public SynchronizerApi.Run.Invoke thenSleepUntil(@NonNull BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    @With
    @Builder
    private static final class GetAction<T> implements SynchronizerApi.Get.WakeSleepInvoke<T>
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
        public SynchronizerApi.Get.SleepInvoke<T> andWakeOthers()
        {
            return andWakeOthersIf(t -> true);
        }

        @Override
        public SynchronizerApi.Get.SleepInvoke<T> andWakeOthersIf(@NonNull Predicate<T> predicate)
        {
            return withWakeOthersPredicate(predicate);
        }

        @Override
        public SynchronizerApi.Get.Invoke<T> thenSleepUntil(@NonNull BooleanSupplier state)
        {
            return withPostState(state);
        }
    }

    @Builder
    private static class RunGetChoice implements SynchronizerApi.RunGet
    {
        @NonNull
        protected final Lock lock;

        private final BooleanSupplier preState;

        @Override
        public SynchronizerApi.Run.WakeSleepInvoke run(@NonNull Runnable runnable)
        {
            return RunAction.builder()
                .lock(lock)
                .preState(preState)
                .runnable(runnable)
                .build();
        }

        @Override
        public <T> SynchronizerApi.Get.WakeSleepInvoke<T> get(@NonNull Supplier<T> getter)
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
            // TODO get rid of this: add @NonNull to state param and fields, use ()->true instead
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

    private final Lock lock;

    /**
     * Creates a new {@code Synchronizer} instance.
     */
    public Synchronizer()
    {
        lock = new Lock();
    }

    @Override
    public SynchronizerApi.Run.WakeSleepInvoke run(@NonNull Runnable runnable)
    {
        return runGetChoiceBuilder().build()
            .run(runnable);
    }

    @Override
    public <T> SynchronizerApi.Get.WakeSleepInvoke<T> get(@NonNull Supplier<T> getter)
    {
        return runGetChoiceBuilder().build()
            .get(getter);
    }

    @Override
    public SynchronizerApi.RunGet sleepUntil(@NonNull BooleanSupplier state)
    {
        return runGetChoiceBuilder().preState(state)
            .build();
    }

    private RunGetChoice.RunGetChoiceBuilder runGetChoiceBuilder()
    {
        return RunGetChoice.builder()
            .lock(lock);
    }
}
