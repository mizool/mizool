package com.github.mizool.core;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface Identifiable<T extends Identifiable<T>>
{
    /**
     * @return the ID, may be {@code null} (e.g. because the Identifiable is in the process of being created)
     */
    @Nullable Identifier<T> getId();

    /**
     * @return the non-null ID
     *
     * @throws IllegalStateException if the ID is null, e.g. because the Identifiable is in the process of being created
     */
    default Identifier<T> obtainId()
    {
        var id = getId();
        if (id == null)
        {
            throw new IllegalStateException();
        }

        return id;
    }
}
