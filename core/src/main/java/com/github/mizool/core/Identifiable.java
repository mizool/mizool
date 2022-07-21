package com.github.mizool.core;

public interface Identifiable<T extends Identifiable>
{
    /**
     * @deprecated use getId() instead
     */
    @Deprecated(forRemoval = true)
    default Identifier<T> getIdentifier()
    {
        return getId();
    }

    default Identifier<T> getId()
    {
        return getIdentifier();
    }
}
