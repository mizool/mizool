package com.github.mizool.core.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Properties;

import lombok.experimental.UtilityClass;

/**
 * Factory for source instances.
 */
@UtilityClass
public class Sources
{
    /**
     * Creates a source without any properties.
     */
    public Source blank()
    {
        return new PropertySource(new Properties());
    }

    /**
     * Creates a source encapsulating access to the current system properties.
     */
    public Source systemProperties()
    {
        return new PropertySource(System.getProperties());
    }

    /**
     * Creates a source encapsulating access to the given {@link Properties} instance.
     */
    public Source from(Properties properties)
    {
        return new PropertySource(properties);
    }

    /**
     * Consumes the given reader and returns a source encapsulating access to its properties.
     *
     * @param reader the reader to load from
     *
     * @throws UncheckedIOException if reading failed
     */
    public Source from(Reader reader)
    {
        try
        {
            var properties = new Properties();
            properties.load(reader);
            return from(properties);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Consumes the given input stream and returns a source encapsulating access to its properties.
     *
     * @param inputStream the input stream to load from
     * @param charset the charset to use
     *
     * @throws UncheckedIOException if reading failed
     */
    public Source from(InputStream inputStream, Charset charset)
    {
        try (InputStreamReader reader = new InputStreamReader(inputStream, charset))
        {
            return from(reader);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates a source that merges two sources. For keys present in both sources, the ones from {@code primary} take
     * precedence.
     */
    public Source join(Source primary, Source fallback)
    {
        return new AdditiveSource(primary, fallback);
    }
}
