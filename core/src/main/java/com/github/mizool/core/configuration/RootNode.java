package com.github.mizool.core.configuration;

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
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class RootNode implements HasChildren
{
    private final Source source;

    public PropertyNode child(String key)
    {
        return new PropertyNode(this.source, key);
    }

    /**
     * Creates a new root node that incorporates the current node's properties and the ones loaded from the given
     * reader. For keys present in both sets of data, the ones from {@code reader} take precedence.
     *
     * @deprecated Use {@link Sources#join(Source, Source)} together with {@link Sources#from(Properties)} instead
     *
     * @param reader the reader to load from
     *
     * @throws UncheckedIOException if reading failed
     */
    @Deprecated(since = "8.3", forRemoval = true)
    public RootNode add(Reader reader)
    {
        var readerSource = Sources.from(reader);
        var joinedSource = Sources.join(readerSource, source);
        return new RootNode(joinedSource);
    }

    /**
     * Creates a new root node that incorporates the current node's properties and the ones loaded from the given input
     * stream. For keys present in both sets of data, the ones from {@code inputStream} take precedence.
     *
     * @deprecated Use {@link Sources#join(Source, Source)} together with {@link Sources#from(InputStream, Charset)} instead
     *
     * @param inputStream the input stream to load from
     * @param charset the charset to use
     *
     * @throws UncheckedIOException if reading failed
     */
    @Deprecated(since = "8.3", forRemoval = true)
    public RootNode add(InputStream inputStream, Charset charset)
    {
        var inputStreamSource = Sources.from(inputStream, charset);
        var joinedSource = Sources.join(inputStreamSource, source);
        return new RootNode(joinedSource);
    }

    @Override
    public String toString()
    {
        return "RootNode";
    }
}
