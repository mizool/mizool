package com.github.mizool.core.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * The root node of a set of properties.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RootNode implements HasChildren
{
    private final Source source;

    public PropertyNode child(String key)
    {
        return new PropertyNode(this.source, key);
    }

    @Override
    public String toString()
    {
        return "RootNode";
    }
}
