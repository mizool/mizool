package com.github.mizool.core;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@UtilityClass
@NullMarked
public class Lookup
{
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Single<T>
    {
        private final @Nullable T value;

        public <R> Single<R> via(Function<T, R> route)
        {
            R result = null;
            if (value != null)
            {
                result = route.apply(value);
            }

            return new Single<>(result);
        }

        public <R> Multi<R> usingList(Function<T, ? extends List<R>> route)
        {
            List<R> result = null;
            if (value != null)
            {
                result = route.apply(value);
            }

            return new Multi<>(result);
        }

        public Single<T> filter(Predicate<Single<T>> predicate)
        {
            T result = predicate.test(this)
                ? value
                : null;
            return new Single<>(result);
        }

        public @Nullable T getOrNull()
        {
            return value;
        }

        public T getOr(T other)
        {
            return value != null
                ? value
                : other;
        }

        public List<T> asListOrEmptyList()
        {
            return value != null
                ? List.of(value)
                : List.of();
        }

        public boolean isNull()
        {
            return value == null;
        }

        public boolean isNotNull()
        {
            return value != null;
        }

        public boolean satisfies(Predicate<@NonNull T> predicate)
        {
            return value != null && predicate.test(value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Multi<T>
    {
        private final @Nullable List<T> value;

        public <R> Multi<R> via(Function<T, R> route)
        {
            List<R> result = null;
            if (value != null && !value.isEmpty())
            {
                result = value.stream()
                    .map(route)
                    .toList();
            }

            return new Multi<>(result);
        }

        public Single<T> viaFirstIgnoringRemaining()
        {
            T result = null;
            if (value != null && !value.isEmpty())
            {
                result = value.get(0);
            }

            return new Single<>(result);
        }

        public <R> Multi<R> flatMap(Function<T, ? extends List<R>> route)
        {
            List<R> result = getOrEmpty().stream()
                .map(route)
                .flatMap(Collection::stream)
                .toList();
            if (result.isEmpty())
            {
                result = null;
            }

            return new Multi<>(result);
        }

        public Multi<T> filter(Predicate<Single<T>> predicate)
        {
            List<T> result = getOrEmpty().stream()
                .filter(t -> predicate.test(new Single<>(t)))
                .toList();
            if (result.isEmpty())
            {
                result = null;
            }

            return new Multi<>(result);
        }

        public List<T> getOrEmpty()
        {
            List<T> result = List.of();
            if (value != null)
            {
                result = value;
            }

            return result;
        }
    }

    public static <T> Single<T> from(@Nullable T start)
    {
        return new Single<>(start);
    }

    public static <T> Multi<T> from(@Nullable List<T> start)
    {
        return new Multi<>(start);
    }
}
