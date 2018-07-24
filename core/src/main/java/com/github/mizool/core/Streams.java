package com.github.mizool.core;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Streams
{
    public <T> Stream<T> sequential(Iterable<T> iterable)
    {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public <T> Stream<T> parallel(Iterable<T> iterable)
    {
        return StreamSupport.stream(iterable.spliterator(), true);
    }
}