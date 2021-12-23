package com.github.mizool.core;

import java.time.Duration;

import javax.annotation.concurrent.ThreadSafe;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

import com.google.common.base.Stopwatch;

/**
 * Helper class to allow for something to happen at regular intervals. A common use case might be invalidation of
 * in-memory data.<br>
 * <br>
 * Example to invalidate a cache every minute:
 * <pre>
 * {@code
 * private final Interval cacheInvalidationInterval = new Interval(Duration.ofMinutes(1));
 *
 * ...
 *
 * cacheInvalidationInterval.runIfDue(this::invalidateCache);
 * }
 * </pre>
 */
@ThreadSafe
@RequiredArgsConstructor
public class Interval
{
    private final Stopwatch stopwatch = Stopwatch.createStarted();
    private final Duration duration;

    /**
     * Executes the {@code runnable} if the interval is due or <i>past due</i>. The next full interval is started after
     * completion of the runnable.
     *
     * @return {@code true} if the operation was due and the runnable completed.
     */
    @Synchronized
    public boolean runIfDue(Runnable runnable)
    {
        boolean isReached = stopwatch.elapsed()
            .compareTo(duration) > 0;
        if (isReached)
        {
            stopwatch.reset();
            runnable.run();
            stopwatch.start();
        }
        return isReached;
    }
}