package com.github.mizool;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.github.mizool.exception.DataInconsistencyException;
import com.github.mizool.exception.ObjectNotFoundException;
import com.google.common.base.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Optionals
{
    /**
     * Used when a user directly requests or references an object, resulting in an ObjectNotFoundException if it does
     * not exist.
     */
    public final <T> T unwrapUserRequestedObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new ObjectNotFoundException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }

    /**
     * Used when an object can be reasonably expected to exist, resulting in a DataInconsistencyException if it does not
     * exist.
     */
    public final <T> T unwrapRequiredObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new DataInconsistencyException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }
}