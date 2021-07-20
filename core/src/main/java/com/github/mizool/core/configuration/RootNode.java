package com.github.mizool.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * The root node of a set of properties.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class RootNode implements HasChildren
{
    static RootNode createFrom(Properties properties)
    {
        Properties copy = new Properties(properties);
        return new RootNode(copy);
    }

    private final Properties properties;

    public PropertyNode child(String key)
    {
        return new PropertyNode(this.properties, key);
    }

    /**
     * Creates a new root node that incorporates the current node's properties and the ones loaded from the given
     * reader.
     *
     * @param reader the reader to load from
     */
    public RootNode add(Reader reader)
    {
        try
        {
            Properties copy = new Properties(this.properties);
            copy.load(reader);
            return new RootNode(copy);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates a new root node that incorporates the current node's properties and the ones loaded from the given input
     * stream. <br>
     * <br>
     * Note that unlike {@link Properties#load(InputStream)}, this method will not use ISO 8859-1 (the classic encoding
     * of properties files) unless specified.
     *
     * @param inputStream the input stream to load from
     * @param charset the charset to use
     */
    public RootNode add(InputStream inputStream, Charset charset)
    {
        InputStreamReader reader = new InputStreamReader(inputStream, charset);
        return add(reader);
    }

    @Override
    public String toString()
    {
        return "RootNode";
    }
}
