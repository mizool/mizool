package com.github.mizool.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class AutoCloseableDecorator<T> implements AutoCloseable
{
    public interface Closer<T>
    {
        void accept(T t) throws Exception;
    }

    @Getter
    private final T target;

    private final Closer<T> closer;

    @Override
    public void close() throws Exception
    {
        closer.accept(target);
    }
}
