package com.github.mizool.core;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;

/**
 * Ensures an instance is only ever iterated once to avoid performance issues and memory leaks.
 */
public final class Every<T>
{
    private final ImmutableSet<T> instances;

    @Inject
    protected Every(Instance<T> instance)
    {
        this.instances = ImmutableSet.copyOf(instance);
    }

    public Iterable<T> get()
    {
        return instances;
    }
}
