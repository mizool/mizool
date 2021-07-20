package com.github.mizool.core.converter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@UtilityClass
public class Converters
{
    public <D, P> List<D> toDtos(List<P> pojos, Function<P, D> converter)
    {
        List<D> results = null;
        if (pojos != null)
        {
            results = pojos.stream().map(converter).collect(ImmutableList.toImmutableList());
        }
        return results;
    }

    public <R, P> List<R> toRecords(List<P> pojos, Function<P, R> converter)
    {
        List<R> results = null;
        if (pojos != null && !pojos.isEmpty())
        {
            results = pojos.stream().map(converter).collect(ImmutableList.toImmutableList());
        }
        return results;
    }

    /**
     * @deprecated Use {@link Converters#toRecords(List, Function)} instead.
     */
    @Deprecated
    public <R, P> List<R> fromPojos(List<P> pojos, Function<P, R> converter)
    {
        List<R> results = null;
        if (pojos != null && !pojos.isEmpty())
        {
            results = pojos.stream().map(converter).collect(ImmutableList.toImmutableList());
        }
        return results;
    }

    public <D, P> List<P> toPojos(List<D> values, Function<D, P> converter)
    {
        List<P> pojos;
        if (values != null)
        {
            pojos = values.stream().map(converter).collect(ImmutableList.toImmutableList());
        }
        else
        {
            pojos = Collections.emptyList();
        }
        return pojos;
    }

    public <D, P> Set<D> toDtos(Set<P> pojos, Function<P, D> converter)
    {
        Set<D> results = null;
        if (pojos != null)
        {
            results = pojos.stream().map(converter).collect(ImmutableSet.toImmutableSet());
        }
        return results;
    }

    public <R, P> Set<R> toRecords(Set<P> pojos, Function<P, R> converter)
    {
        Set<R> results = null;
        if (pojos != null && !pojos.isEmpty())
        {
            results = pojos.stream().map(converter).collect(ImmutableSet.toImmutableSet());
        }

        return results;
    }

    /**
     * @deprecated Use {@link Converters#toRecords(Set, Function)} instead.
     */
    @Deprecated
    public <R, P> Set<R> fromPojos(Set<P> pojos, Function<P, R> converter)
    {
        Set<R> results = null;
        if (pojos != null && !pojos.isEmpty())
        {
            results = pojos.stream().map(converter).collect(ImmutableSet.toImmutableSet());
        }

        return results;
    }

    public <D, P> Set<P> toPojos(Set<D> values, Function<D, P> converter)
    {
        Set<P> pojos;
        if (values != null)
        {
            pojos = values.stream().map(converter).collect(ImmutableSet.toImmutableSet());
        }
        else
        {
            pojos = Collections.emptySet();
        }
        return pojos;
    }
}
