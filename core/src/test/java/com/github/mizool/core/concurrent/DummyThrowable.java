package com.github.mizool.core.concurrent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
final class DummyThrowable extends Throwable
{
    private final String message;

    public DummyThrowable()
    {
        this(null);
    }
}
