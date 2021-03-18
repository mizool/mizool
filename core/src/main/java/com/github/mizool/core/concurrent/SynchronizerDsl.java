package com.github.mizool.core.concurrent;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface SynchronizerDsl
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
        void invoke();
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
        T invoke();
    }
}
