package com.github.mizool.core.concurrent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class DummyError extends Error
{
    private final String message;

    public DummyError()
    {
        this(null);
    }
}
