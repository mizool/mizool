package com.github.mizool.core;

public interface Identifiable<T extends Identifiable>
{
    /**
     * @deprecated use getId() instead
     */
    @Deprecated
    default Identifier<T> getIdentifier()
    {
        return getId();
    }

    default Identifier<T> getId()
    {
        return getIdentifier();
    }
}
