package com.github.mizool.technology.jcache.safe;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.inject.Singleton;

import lombok.Data;

@Singleton
@Data
class CacheWatchdogState
{
    private final AtomicLong timestamp = new AtomicLong(0);
    private final AtomicBoolean cacheResetRequired = new AtomicBoolean(false);

    public void setTimestamp(long timestamp)
    {
        this.timestamp.set(timestamp);
    }

    public long getTimestamp()
    {
        return timestamp.get();
    }

    public boolean toggleCacheResetToRequired()
    {
        return this.cacheResetRequired.compareAndSet(false, true);
    }

    public boolean toggleCacheResetToNotRequired()
    {
        return this.cacheResetRequired.compareAndSet(true, false);
    }
}
