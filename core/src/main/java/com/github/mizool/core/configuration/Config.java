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
     * Creates a root node without any properties. Usually used as a starting point followed by multiple {@code
     * RootNode.add()} calls.
     *
     * @see RootNode#add(Reader)
     * @see RootNode#add(InputStream, Charset)
     */
    public RootNode blank()
    {
        return RootNode.createFrom(new Properties());
    }

    /**
     * Creates a root node encapsulating access to the current system properties.
     */
    public RootNode systemProperties()
    {
        return RootNode.createFrom(System.getProperties());
    }

    /**
     * Creates a root node encapsulating access to the given {@link Properties} instance.
     */
    public RootNode from(Properties properties)
    {
        return RootNode.createFrom(properties);
    }
}
