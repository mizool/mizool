package com.github.mizool.core;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

import com.google.common.base.Stopwatch;

/**
 * Helper class to allow for something to happen after a certain amount of time. A common uses cases might be
 * invalidation of in memory data.<br>
 * <br>
 * Example to invalidate a cache every minute:
 * <pre>
 * {@code
 * private final Interval cacheInvalidationInterval = new Interval(Duration.ofMinutes(1));
 *
 * ...
 *
 * if (cacheInvalidationInterval.isReached())
 * {
 *     invalidateCache();
 * }
 * }
 * </pre>
 */
@RequiredArgsConstructor
public class Interval
{
    private final Stopwatch stopwatch = Stopwatch.createStarted();
    private final Duration duration;

    public boolean isReached()
    {
        boolean isReached = stopwatch.elapsed()
            .compareTo(duration) > 0;
        if (isReached)
        {
            stopwatch.reset();
            stopwatch.start();
        }
        return isReached;
    }
}