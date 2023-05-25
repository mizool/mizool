package com.github.mizool.core;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;

import jakarta.enterprise.inject.Instance;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.common.base.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MockitoExtensions
{
    @SuppressWarnings("unchecked")
    public static <T> Identifier<T> mockIdentifier()
    {
        return mock(Identifier.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Identifier<T> anyIdentifierOf(Class<T> tClass)
    {
        return nullable(Identifier.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> anyOptionalOf(Class<T> tClass)
    {
        return nullable(Optional.class);
    }

    public static <T> Instance<T> mockInstance(Iterable<T> values)
    {
        Instance<T> mockedInstance = mock(Instance.class);
        when(mockedInstance.iterator()).then(invocation -> values.iterator());
        return mockedInstance;
    }

    public static <T> Instance<T> mockInstance(T... values)
    {
        return mockInstance(Arrays.asList(values));
    }

    public static Clock mockClock(ZonedDateTime zonedDateTime)
    {
        return Clock.fixed(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }
}
