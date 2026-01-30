package com.github.mizool.core.configuration;

import java.util.Properties;

import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
final class PropertySource implements Source
{
    private final Properties properties;

    @Override
    public @Nullable String getRawValue(String key)
    {
        return properties.getProperty(key);
    }
}
