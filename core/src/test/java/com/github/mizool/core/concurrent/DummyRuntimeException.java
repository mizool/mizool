package com.github.mizool.core.concurrent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class DummyRuntimeException extends RuntimeException
{
    private final String message;

    public DummyRuntimeException()
    {
        this(null);
    }
}
