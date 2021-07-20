package com.github.mizool.core.configuration;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import com.github.mizool.core.exception.ConfigurationException;

/**
 * An object that allows type-safe access to the value of a {@link PropertyNode}.
 */
public final class Value<T>
{
    private final Function<String, T> valueConversionFunction;
    private final String key;
    private final Properties properties;

    Value(Properties properties, String key, Function<String, T> valueConversionFunction)
    {
        this.valueConversionFunction = valueConversionFunction;
        this.key = key;
        this.properties = new Properties(properties);
    }

    /**
     * @throws ConfigurationException if the present, non-empty value could not be parsed according to the type
     * specified
     */
    public Optional<T> read()
    {
        String valueOrNull = properties.getProperty(key);
        try
        {
            return Optional.ofNullable(valueOrNull)
                .filter(s -> !s.isEmpty())
                .map(valueConversionFunction);
        }
        catch (RuntimeException e)
        {
            throw new ConfigurationException("Could not parse value of property '" + key + "'", e);
        }
    }

    /**
     * @throws ConfigurationException if the present, non-empty value could not be parsed according to the type
     * specified
     */
    public T obtain()
    {
        return read().orElseThrow(() -> new ConfigurationException("Property '" + key + "' must be set"));
    }
}
