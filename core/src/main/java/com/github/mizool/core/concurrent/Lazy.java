package com.github.mizool.core.concurrent;

import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

/**
 * A value container where the value is lazily obtained from the given {@link Supplier} on the first {@link Lazy#get()}
 * call. All subsequent {@link Lazy#get()} calls will return the same value.<br>
 * There is at most <i>one</i> call to the {@link Supplier}. The state whether the {@link Supplier} was called in the
 * past is stored within the {@link Lazy} instance.<br>
 * This means that {@code null} is handled transparently: if the {@link Supplier} returns {@code null}, the {@link Lazy}
 * instance will return {@code null} for all subsequent calls of {@link Lazy#get()} as well.
 * This class only handles the value. Exceptions thrown by the {@link Supplier} are simply passed on and the internal
 * state of {@link Lazy} does not change.
 */
@RequiredArgsConstructor
public final class Lazy<T> implements Supplier<T>
{
    @NonNull
    private final Supplier<T> supplier;

    private boolean initialized;
    private T value;

    @Synchronized
    public T get()
    {
        if (!initialized)
        {
            value = supplier.get();
            initialized = true;
        }
        return value;
    }
}