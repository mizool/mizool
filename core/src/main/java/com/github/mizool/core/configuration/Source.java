package com.github.mizool.core.configuration;

import org.jspecify.annotations.Nullable;

public interface Source
{
    @Nullable String getRawValue(String key);
}
