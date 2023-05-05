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
 *         <ul>
 *             <li>
 *                 a {@link Runnable} ({@code run}) or {@link Supplier} ({@code get})
 *             </li>
 *         </ul><br>
 *     </li>
 *     <li>Sleep until <i>condition</i> is {@code true}
 *         <ul>
 *             <li>
 *                 Whenever the thread receives a {@linkplain #wakeOthers() wake} call, it checks this condition. If
 *                 {@code true}, the thread stops sleeping and continues its action chain. If {@code false}, it resumes
 *                 sleeping.
 *             </li>
 *             <li>
 *                 By default, the condition will only be checked when a wake call happens. However, you can also
 *                 specify the duration of an interval after which the thread should re-check the condition on its own
 *                 and then stop/resume sleep as explained above.
 *             </li>
 *             <li>
 *                 Can be used before ({@code sleepUntil}) and/or after ({@code thenSleepUntil}) the main action.
 *             </li>
 *             <li>
 *                 Sleeping is implemented in line with secure coding practices, i.e. {@link Object#wait()} is called
 *                 inside a loop to ensure liveness and safety (see
 *                 <a href="https://wiki.sei.cmu.edu/confluence/display/java/THI03-J.+Always+invoke+wait%28%29+and+await%28%29+methods+inside+a+loop">SEI
 *                 CERT rule THI03-J</a> for details).
 *             </li>
 *             <li>
 *                 If the thread is interrupted while waiting, {@link UncheckedInterruptedException} is thrown and the
 *                 thread's interrupted flag is re-set.
 *             </li>
 *         </ul><br>
 *     </li>
 *     <li>Wake other threads
 *         <ul>
 *             <li>
 *                 Wakes each sleeping thread (by calling {@link Object#notifyAll()}), causing it to check its condition
 *                 (see above).
 *             </li>
 *             <li>
 *                 If the main action of this chain is a {@code Supplier<T>}, the calling code can decide whether waking
 *                 takes place by using {@code andWakeOthersIf(Predicate<T>)} instead.
 *             </li>
 *         </ul><br>
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
 * <br>
 * <h3>Note on terminology</h3>
 * This class intentionally uses the verbs "sleep" and "wake". If it used "wait" and "notify" instead, its methods would
 * all too easily be confused with methods in java.lang.Object which, if invoked on the objects returned by chained
 * methods, could cause deadlocks.
 */
public final class Synchronizer implements SynchronizerApi.SleepRunGet
{
    /**
     * Specifies how to sleep. More precisely, it specifies when to stop sleeping (when, upon being {@linkplain
     * #wakeOthers() woken}, the condition returns {@code true}). Optionally, also specifies an amount of time after
     * which the condition should be re-checked even if no wake call happens.
     */
    @Getter
    private static final class SleepSpec
    {
        /**
         * A default value for action chains that do not use sleeping. This is implemented using a condition that simply
         * returns {@code true}: as mentioned in the API, Synchronizer will skip sleeping in that case.
         */
        private static final SleepSpec NO_SLEEP = new SleepSpec(() -> true, null);

        /**
         * The condition that must be {@code true} in order to stop sleeping.
         */
        private final BooleanSupplier condition;

        /**
         * The timeout to pass when calling {@link Object#wait(long)}. 0 means "indefinite".
         */
        private final long waitTimeoutMillis;

        /**
         * @param condition the condition that must be {@code true} in order to stop sleeping
         * @param checkInterval how often the condition should be re-checked even if no wake call happens, or
         * {@code null} to only check on wake calls
         */
        @Builder
        private SleepSpec(@NonNull BooleanSupplier condition, @Nullable Duration checkInterval)
        {
            this.condition = condition;
            waitTimeoutMillis = toWaitTimeoutMillis(checkInterval);
        }

        /**
         * If the user doesn't specify an interval, we default to zero, which wait() interprets as "indefinite".
         */
        private long toWaitTimeoutMillis(Duration interval)
        {
            if (interval == null)
            {
                return 0;
            }
            return interval.toMillis();
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
        public SynchronizerApi.Run.Invoke thenSleepUntil(@NonNull BooleanSupplier state, Duration checkInterval)
        {
            return withSleepAfter(new SleepSpec(state, checkInterval));
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
        public SynchronizerApi.Get.Invoke<T> thenSleepUntil(@NonNull BooleanSupplier state, Duration checkInterval)
        {
            return withSleepAfter(new SleepSpec(state, checkInterval));
        }
    }

    @Builder
    private static class RunGetChoice implements SynchronizerApi.RunGetInvoke
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
                        // Wait according to the given sleep interval or indefinitely (see SleepSpec constructor)
                        wait(sleepSpec.waitTimeoutMillis);
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
    public SynchronizerApi.RunGetInvoke sleepUntil(@NonNull BooleanSupplier state, Duration checkInterval)
    {
        return runGetChoiceBuilder().sleepBefore(new SleepSpec(state, checkInterval))
            .build();
    }

    private RunGetChoice.RunGetChoiceBuilder runGetChoiceBuilder()
    {
        return RunGetChoice.builder()
            .lock(lock);
    }
}
