package com.github.mizool.core;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.ReductionException;

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

    /**
     * Accumulator for {@link Stream#reduce(BinaryOperator)} that expects all values of the {@link Stream} to be equal
     * to each other according to {@link Objects#equals(Object, Object)}.<br>
     * <br>
     * A {@link ReductionException} will be thrown during reduction if one or more of the values in the {@link Stream}
     * are not equal according to {@link Objects#equals(Object, Object)}.
     */
    public <T> BinaryOperator<T> expectingEqualValues()
    {
        return (t, u) -> {
            if (!Objects.equals(t, u))
            {
                throw new ReductionException("\"" + u + "\" is not equal to \"" + t + "\"");
            }
            return t;
        };
    }
}
