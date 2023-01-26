package com.github.mizool.core.concurrent;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;

import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.github.mizool.core.validation.Nullable;

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
    @Getter
    private static final class SleepSpec
    {
        /**
         * Why "() -> true"? As mentioned in the API, if the condition returns true, we never even start sleeping.
         */
        private static final SleepSpec NO_SLEEP = new SleepSpec(() -> true, null);

        private final BooleanSupplier condition;
        private final long timeoutMillis;

        @Builder
        private SleepSpec(@NonNull BooleanSupplier condition, @Nullable Duration timeout)
        {
            this.condition = condition;

            // If the user doesn't specify a timeout, we default to zero, which wait() interprets as "indefinite".
            this.timeoutMillis = timeout == null
                ? 0
                : timeout.toMillis();
        }
    }

    private static final class RunAction implements SynchronizerApi.Run.WakeSleepInvoke
    {
        private final Lock lock;

        private final SleepSpec sleepBefore;

        private final Runnable runnable;

        @With
        private final SleepSpec sleepAfter;

        @With
        private final boolean wakeOthers;

        @Builder
        public RunAction(
            @NonNull Lock lock,
            SleepSpec sleepBefore,
            @NonNull Runnable runnable,
            SleepSpec sleepAfter,
            boolean wakeOthers)
        {
            this.lock = lock;
            this.sleepBefore = useValueOrDefault(sleepBefore, SleepSpec.NO_SLEEP);
            this.runnable = runnable;
            this.sleepAfter = useValueOrDefault(sleepAfter, SleepSpec.NO_SLEEP);
            this.wakeOthers = wakeOthers;
        }

        @Override
        public void invoke()
        {
            synchronized (lock)
            {
                lock.sleep(sleepBefore);

                runnable.run();

                if (wakeOthers)
                {
                    lock.notifyAll();
                }

                lock.sleep(sleepAfter);
            }
        }

        @Override
        public SynchronizerApi.Run.SleepInvoke andWakeOthers()
        {
            return withWakeOthers(true);
        }

        @Override
        public SynchronizerApi.Run.Invoke thenSleepUntil(@NonNull BooleanSupplier state, Duration timeout)
        {
            return withSleepAfter(new SleepSpec(state, timeout));
        }
    }

    @Builder
    private static final class GetAction<T> implements SynchronizerApi.Get.WakeSleepInvoke<T>
    {
        private final Lock lock;

        private final SleepSpec sleepBefore;

        private final Supplier<T> getter;

        @With
        private final Predicate<T> wakeOthersPredicate;

        @With
        private final SleepSpec sleepAfter;

        public GetAction(
            @NonNull Lock lock,
            SleepSpec sleepBefore,
            @NonNull Supplier<T> getter,
            Predicate<T> wakeOthersPredicate,
            SleepSpec sleepAfter)
        {
            this.lock = lock;
            this.sleepBefore = useValueOrDefault(sleepBefore, SleepSpec.NO_SLEEP);
            this.getter = getter;
            this.wakeOthersPredicate = useValueOrDefault(wakeOthersPredicate, t -> false);
            this.sleepAfter = useValueOrDefault(sleepAfter, SleepSpec.NO_SLEEP);
        }

        @Override
        public T invoke()
        {
            synchronized (lock)
            {
                lock.sleep(sleepBefore);

                T result = getter.get();

                if (wakeOthersPredicate.test(result))
                {
                    lock.notifyAll();
                }

                lock.sleep(sleepAfter);

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
        public SynchronizerApi.Get.Invoke<T> thenSleepUntil(@NonNull BooleanSupplier state, Duration timeout)
        {
            return withSleepAfter(new SleepSpec(state, timeout));
        }
    }

    @Builder
    private static class RunGetChoice implements SynchronizerApi.RunGet
    {
        @NonNull
        protected final Lock lock;

        private final SleepSpec sleepBefore;

        @Override
        public SynchronizerApi.Run.WakeSleepInvoke run(@NonNull Runnable runnable)
        {
            return RunAction.builder()
                .lock(lock)
                .sleepBefore(sleepBefore)
                .runnable(runnable)
                .build();
        }

        @Override
        public <T> SynchronizerApi.Get.WakeSleepInvoke<T> get(@NonNull Supplier<T> getter)
        {
            return GetAction.<T>builder()
                .lock(lock)
                .sleepBefore(sleepBefore)
                .getter(getter)
                .build();
        }
    }

    @RequiredArgsConstructor
    private static class Lock
    {
        protected void sleep(@NonNull SleepSpec sleepSpec)
        {
            // The caller should already hold this lock, so this is a no-op. We keep it to make code analyzers happy.
            synchronized (this)
            {
                try
                {
                    while (!sleepSpec.condition.getAsBoolean())
                    {
                        // If timeout == null, maxSleepMillis will be 0, which wait() interprets as "indefinite".
                        wait(sleepSpec.timeoutMillis);
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

    private static <T> T useValueOrDefault(T value, @NonNull T defaultValue)
    {
        if (value != null)
        {
            return value;
        }
        return defaultValue;
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
    public SynchronizerApi.RunGet sleepUntil(@NonNull BooleanSupplier state, Duration timeout)
    {
        return runGetChoiceBuilder().sleepBefore(new SleepSpec(state, timeout))
            .build();
    }

    private RunGetChoice.RunGetChoiceBuilder runGetChoiceBuilder()
    {
        return RunGetChoice.builder()
            .lock(lock);
    }
}
