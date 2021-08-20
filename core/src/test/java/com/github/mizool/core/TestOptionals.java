package com.github.mizool.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

public class TestOptionals
{
    @Test(dataProvider = "optionalStreams")
    public <T> void testStreamPresentValue(String testName, Collection<Optional<T>> input, Iterable<T> expected)
    {
        Iterable<T> actual = input.stream()
            .flatMap(Optionals::streamPresentValue)
            .collect(ImmutableSet.toImmutableSet());
        assertThat(actual).containsOnlyElementsOf(expected);
    }

    @DataProvider
    private Object[][] optionalStreams()
    {
        Set<Void> emptyList = Collections.emptySet();

        Optional<Void> empty = Optional.empty();
        Optional<String> a = Optional.of("a");
        Optional<String> b = Optional.of("b");
        Optional<String> c = Optional.of("c");

        return new Object[][]{
            { "empty stream", emptyList, emptyList },
            { "isolated empty optional", ImmutableSet.of(empty), emptyList },
            { "single value", ImmutableSet.of(a), ImmutableSet.of("a") },
            { "multiple values", ImmutableSet.of(a, b, c), ImmutableSet.of("a", "b", "c") },
            { "empty optional among values", ImmutableSet.of(a, empty, c), ImmutableSet.of("a", "c") },
            { "multiple equal values", ImmutableSet.of(a, a), ImmutableSet.of("a", "a") }
        };
    }
}
