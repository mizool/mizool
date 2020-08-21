/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     *     specified
     */
    public Optional<T> read()
    {
        String valueOrNull = properties.getProperty(key);
        try
        {
            return Optional.ofNullable(valueOrNull).filter(s -> !s.isEmpty()).map(valueConversionFunction);
        }
        catch (RuntimeException e)
        {
            throw new ConfigurationException("Could not parse value of property '" + key + "'", e);
        }
    }

    /**
     * @throws ConfigurationException if the present, non-empty value could not be parsed according to the type
     *     specified
     */
    public T obtain()
    {
        return read().orElseThrow(() -> new ConfigurationException("Property '" + key + "' must be set"));
    }
}
