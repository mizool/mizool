package com.github.mizool.core.concurrent;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.exception.UncheckedInterruptedException;
import com.google.common.base.Predicates;

/**
 * Encapsulates synchronization and wait/notify functionality.
 *
 * <h3>Introduction</h3>
 * By calling {@link #define()}, the calling code sets up a chain of actions that is completed with {@code invoke()}.
 * All those actions are performed in one synchronized block using a lock which is private to the
 * {@code FluentSynchronizer} instance.
 *
 * <p>With its fluent API and encapsulated lock, this class can help increasing both the readability and robustness of
 * concurrent algorithms. Still, care must be taken to avoid deadlocks, just as if using {@code synchronized} blocks
 * and {@link Object#wait()} / {@link Object#notifyAll()} directly.
 *
 * <h3>Actions</h3>
 * {@code FluentSynchronizer} provides the following actions:
 *
 * <ul>
 *     <li>Invoke custom code
 *         <ul>
 *             <li>
 *                 {@link FluentSynchronizerApi.Void#run(Runnable) run(Runnable)} to run arbitrary code
 *             </li>
 *             <li>
 *                 {@link FluentSynchronizerApi.Void#get(Supplier) get(Supplier)} to set a value as the <b>chain
 *                 result</b> which is passed to subsequent actions
 *             </li>
 *             <li>
 *                 {@link FluentSynchronizerApi.Value#map(Function) map(Function)} to process or transform the current
 *                 <i>chain result</i>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>Sleep until <i>condition</i> is {@code true}
 *         <ul>
 *             <li>
 *                 Whenever the thread receives a {@linkplain FluentSynchronizerApi.Common#wakeOthers() wake} call, it
 *                 checks this condition. If {@code true}, the thread stops sleeping and continues its action chain. If
 *                 {@code false}, it resumes sleeping.
 *             </li>
 *             <li>
 *                 By default, the condition will only be checked when a wake call happens. However, you can also
 *                 specify the duration of an interval after which the thread should re-check the condition on its own
 *                 and then stop/resume sleep as explained above.
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
 *         </ul>
 *     </li>
 *     <li>Wake other threads
 *         <ul>
 *             <li>
 *                 {@link FluentSynchronizerApi.Value#wakeOthers() wakeOthers()} wakes each sleeping chain (by calling
 *                 {@link Object#notifyAll()}), causing it to check its condition (see above).
 *             </li>
 *             <li>
 *                 If a <i>{@linkplain FluentSynchronizerApi.Void#get(Supplier) chain result}</i> was set, the calling
 *                 code can decide whether waking takes place by using the
 *                 {@link FluentSynchronizerApi.Value#wakeOthersIf(Predicate) wakeOthersIf(Predicate)} action.
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <h3>Example usage</h3>
 * <pre>{@code synchronizer.define()
 *     .run(...)
 *     .sleepUntil(...)
 *     .invoke();
 *
 * synchronizer.define()
 *     .sleepUntil(...)
 *     .run(...)
 *     .wakeOthers()
 *     .invoke();
 *
 * ResultClass result = synchronizer.define()
 *     .get(...)
 *     .invoke();
 *
 * synchronizer.define()
 *     .get(...)
 *     .wakeOthersIf(Spline::isReticulated)
 *     .discardResult()
 *     .invoke();}</pre>
 *
 * <h3>Note on terminology</h3>
 * This class intentionally uses the verbs "sleep" and "wake". If it used "wait" and "notify" instead, its methods would
 * all too easily be confused with methods in java.lang.Object which, if invoked on the objects returned by chained
 * methods, could cause deadlocks.
 */
@NullMarked
public final class FluentSynchronizer
{
    @RequiredArgsConstructor
    private abstract static class Element
    {
        protected final Lock lock;
        private final @Nullable Element previous;

        public final @Nullable Object invoke()
        {
            synchronized (lock)
            {
                Object result = null;
                if (previous != null)
                {
                    result = previous.invoke();
                }

                return doInvoke(result);
            }
        }

        protected abstract @Nullable Object doInvoke(@Nullable Object input);
    }

    private static final class RunElement extends Element
    {
        private final Runnable runnable;

        public RunElement(Lock lock, @Nullable Element previous, Runnable runnable)
        {
            super(lock, previous);
            this.runnable = runnable;
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            runnable.run();
            return input;
        }
    }

    private static final class WakeElement extends Element
    {
        private final Predicate<@Nullable Object> predicate;

        public WakeElement(Lock lock, @Nullable Element previous, Predicate<@Nullable Object> predicate)
        {
            super(lock, previous);
            this.predicate = predicate;
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            // The caller should already hold this lock, so this is a no-op. We keep it to make code analyzers happy.
            synchronized (lock)
            {
                if (predicate.test(input))
                {
                    lock.notifyAll();
                }
            }
            return input;
        }
    }

    private static final class SleepElement extends Element
    {
        private final SleepSpec sleepSpec;

        public SleepElement(Lock lock, @Nullable Element previous, SleepSpec sleepSpec)
        {
            super(lock, previous);
            this.sleepSpec = sleepSpec;
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            // The caller should already hold this lock, so this is a no-op. We keep it to make code analyzers happy.
            synchronized (lock)
            {
                lock.sleep(sleepSpec);
                return input;
            }
        }
    }

    private static final class GetElement extends Element
    {
        private final Supplier<@Nullable Object> getter;

        public GetElement(Lock lock, @Nullable Element previous, Supplier<@Nullable Object> getter)
        {
            super(lock, previous);
            this.getter = getter;
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            return getter.get();
        }
    }

    private static final class MappingElement extends Element
    {
        private final Function<@Nullable Object, @Nullable Object> function;

        @SuppressWarnings("java:S4276") // Can't use UnaryOperator here: the two type args can be distinct at runtime
        public MappingElement(
            Lock lock,
            @Nullable Element previous,
            Function<@Nullable Object, @Nullable Object> function)
        {
            super(lock, previous);
            this.function = function;
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            return function.apply(input);
        }
    }

    private static final class DiscardingElement extends Element
    {
        public DiscardingElement(Lock lock, @Nullable Element previous)
        {
            super(lock, previous);
        }

        @Override
        protected @Nullable Object doInvoke(@Nullable Object input)
        {
            return null;
        }
    }

    @RequiredArgsConstructor
    private abstract static class AbstractBuilder<X>
    {
        protected final Lock lock;
        protected final @Nullable Element tailElement;

        public X run(Runnable runnable)
        {
            var element = new RunElement(lock, tailElement, runnable);
            return makeSuccessor(element);
        }

        protected abstract X makeSuccessor(Element element);

        public X sleepUntil(BooleanSupplier state)
        {
            return sleepUntil(state, null);
        }

        public X sleepUntil(BooleanSupplier state, @Nullable Duration checkInterval)
        {
            var element = new SleepElement(lock, tailElement, new SleepSpec(state, checkInterval));
            return makeSuccessor(element);
        }

        public X wakeOthers()
        {
            return wakeOthersIf(Predicates.alwaysTrue());
        }

        public X wakeOthersIf(Predicate<Object> predicate)
        {
            var element = new WakeElement(lock, tailElement, predicate);
            return makeSuccessor(element);
        }
    }

    private static final class VoidBuilder extends AbstractBuilder<VoidBuilder> implements FluentSynchronizerApi.VoidInvoke
    {
        public VoidBuilder(Lock lock, @Nullable Element tailElement)
        {
            super(lock, tailElement);
        }

        public void invoke()
        {
            if (tailElement == null)
            {
                // FluentSynchronizerApi should ensure that invoke() is only available after a chain element was added
                throw new CodeInconsistencyException();
            }

            tailElement.invoke();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> FluentSynchronizerApi.Value<T> get(Supplier<T> supplier)
        {
            Supplier<Object> uncheckedSupplier = (Supplier<Object>) supplier;

            var element = new GetElement(lock, tailElement, uncheckedSupplier);

            return (FluentSynchronizerApi.Value<T>) new ValueBuilder(lock, element);
        }

        @Override
        protected VoidBuilder makeSuccessor(Element element)
        {
            return new VoidBuilder(lock, element);
        }
    }

    private static final class ValueBuilder extends AbstractBuilder<ValueBuilder>
        implements FluentSynchronizerApi.Value<@Nullable Object>
    {
        public ValueBuilder(Lock lock, @Nullable Element tailElement)
        {
            super(lock, tailElement);
        }

        @Override
        public @Nullable Object invoke()
        {
            if (tailElement == null)
            {
                // FluentSynchronizerApi should ensure that invoke() is only available after a chain element was added
                throw new CodeInconsistencyException();
            }

            return tailElement.invoke();
        }

        @Override
        @SuppressWarnings({"unchecked", "java:S4276"})
        public <R> FluentSynchronizerApi.Value<R> map(Function<Object, R> function)
        {
            // S4276 recommends UnaryOperator, but we can't use that here as the type args can be distinct at runtime
            var uncheckedFunction = (Function<@Nullable Object, @Nullable Object>) function;

            var element = new MappingElement(lock, tailElement, uncheckedFunction);

            return (FluentSynchronizerApi.Value<R>) makeSuccessor(element);
        }

        @Override
        public FluentSynchronizerApi.VoidInvoke discardResult()
        {
            var element = new DiscardingElement(lock, tailElement);
            return new VoidBuilder(lock, element);
        }

        @Override
        protected ValueBuilder makeSuccessor(Element element)
        {
            return new ValueBuilder(lock, element);
        }
    }

    /**
     * Specifies how to sleep. More precisely, it specifies when to stop sleeping (when, upon being {@linkplain
     * FluentSynchronizerApi.Common#wakeOthers() woken}, the condition returns {@code true}). Optionally, also
     * specifies an amount of time after which the condition should be re-checked even if no wake call happens.
     */
    @Getter
    private static final class SleepSpec
    {
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
        private SleepSpec(@NonNull BooleanSupplier condition, @Nullable Duration checkInterval)
        {
            this.condition = condition;
            waitTimeoutMillis = toWaitTimeoutMillis(checkInterval);
        }

        /**
         * If the user doesn't specify an interval, we default to zero, which wait() interprets as "indefinite".
         */
        private long toWaitTimeoutMillis(@Nullable Duration interval)
        {
            if (interval == null)
            {
                return 0;
            }
            return interval.toMillis();
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

    private final Lock lock;

    /**
     * Creates a new {@code FluentSynchronizer} instance.
     */
    public FluentSynchronizer()
    {
        lock = new Lock();
    }

    public FluentSynchronizerApi.Void define()
    {
        return new VoidBuilder(lock, null);
    }
}
