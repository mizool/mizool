package com.github.mizool.core.configuration;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import lombok.experimental.UtilityClass;

/**
 * Main entry point for the configuration API.
 */
@UtilityClass
public class Config
{
    /**
     * Creates a root node encapsulating access to the given {@link Source} instance.
     */
    public RootNode from(Source source)
    {
        return new RootNode(source);
    }

    /**
     * Creates a root node without any properties. Usually used as a starting point followed by multiple {@code
     * RootNode.add()} calls.
     *
     * @deprecated Use {@link Sources#blank()} instead.
     *
     * @see RootNode#add(Reader)
     * @see RootNode#add(InputStream, Charset)
     */
    @Deprecated(since = "8.3", forRemoval = true)
    public RootNode blank()
    {
        var source = Sources.blank();
        return from(source);
    }

    /**
     * Creates a root node encapsulating access to the current system properties.
     */
    public RootNode systemProperties()
    {
        var source = Sources.systemProperties();
        return from(source);
    }

    /**
     * Creates a root node encapsulating access to the given {@link Properties} instance.
     */
    public RootNode from(Properties properties)
    {
        var source = Sources.from(properties);
        return from(source);
    }

    /**
     * Consumes the given reader and returns a root node encapsulating access to its properties.
     */
    public RootNode from(Reader reader)
    {
        var source = Sources.from(reader);
        return from(source);
    }

    /**
     * Consumes the input stream and returns a root node encapsulating access to its properties.
     *
     * @param inputStream the input stream to load from
     * @param charset the charset to use
     */
    public RootNode from(InputStream inputStream, Charset charset)
    {
        var source = Sources.from(inputStream, charset);
        return from(source);
    }
}
