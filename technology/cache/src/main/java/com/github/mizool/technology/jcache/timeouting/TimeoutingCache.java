package com.github.mizool.technology.jcache.timeouting;

import java.util.concurrent.Callable;

import javax.cache.Cache;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.technology.jcache.common.AbstractDelegatingCache;

@Slf4j
class TimeoutingCache<K, V> extends AbstractDelegatingCache<K, V>
{
    private final TimeoutingExecutor timeoutingExecutor;

    public TimeoutingCache(@NonNull Cache<K, V> target, @NonNull TimeoutingExecutor timeoutingExecutor)
    {
        super(target);
        this.timeoutingExecutor = timeoutingExecutor;
    }

    @Override
    public V get(K key)
    {
        Callable<V> callable = () -> getTarget().get(key);
        V result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void put(K key, V value)
    {
        Runnable runnable = () -> getTarget().put(key, value);
        timeoutingExecutor.execute(runnable);
    }

    @Override
    public boolean remove(K key)
    {
        Callable<Boolean> callable = () -> getTarget().remove(key);
        Boolean result = timeoutingExecutor.execute(callable);
        return result;
    }

    @Override
    public void removeAll()
    {
        Runnable runnable = getTarget()::removeAll;
        timeoutingExecutor.execute(runnable);
    }
}
