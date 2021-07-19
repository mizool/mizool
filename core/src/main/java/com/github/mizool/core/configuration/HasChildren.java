package com.github.mizool.core.configuration;

/**
 * An object that allows retrieving {@link PropertyNode} children.
 */
public interface HasChildren
{
    /**
     * Returns the child {@link PropertyNode} with the given name. Until a value is retrieved, no check is made for the
     * existence of any property.<br>
     * <br>
     * <b>Example:</b> For a node representing {@code timeouts.network}, calling {@code child("connect")} will return a
     * node representing {@code timeouts.network.connect}.
     *
     * @param reference one or more segments of the key to append to the key of the current node
     *
     * @return the child or descendant node, never {@code null}.
     */
    PropertyNode child(String reference);
}
