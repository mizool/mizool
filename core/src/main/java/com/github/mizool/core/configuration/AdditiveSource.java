package com.github.mizool.core.configuration;

import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
final class AdditiveSource implements Source
{
    private final Source primary;
    private final Source fallback;

    @Override
    public @Nullable String getRawValue(String key)
    {
        var primaryValue = primary.getRawValue(key);
        return primaryValue != null
            ? primaryValue
            : fallback.getRawValue(key);
    }
}
