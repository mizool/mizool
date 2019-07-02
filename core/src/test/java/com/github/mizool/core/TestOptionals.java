/**
 * Copyright 2019 incub8 Software Labs GmbH
 * Copyright 2019 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestOptionals
{
    @Test(dataProvider = "optionalStreams")
    public <T> void testStreamPresentValue(String testName, Collection<Optional<T>> input, Iterable<T> expected)
    {
        List<T> actual = input.stream().flatMap(Optionals::streamPresentValue).collect(ImmutableList.toImmutableList());
        assertThat(actual).containsOnlyElementsOf(expected);
    }

    @DataProvider
    private Object[][] optionalStreams()
    {
        List<Void> emptyList = Collections.emptyList();

        Optional<Void> empty = Optional.empty();
        Optional<String> a = Optional.of("a");
        Optional<String> b = Optional.of("b");
        Optional<String> c = Optional.of("c");

        return new Object[][]{
            { "empty stream", emptyList, emptyList },
            { "isolated empty optional", ImmutableList.of(empty), emptyList },
            { "single value", ImmutableList.of(a), ImmutableList.of("a") },
            { "multiple values", ImmutableList.of(a, b, c), ImmutableList.of("a", "b", "c") },
            { "empty optional among values", ImmutableList.of(a, empty, c), ImmutableList.of("a", "c") },
            { "multiple equal values", ImmutableList.of(a, a), ImmutableList.of("a", "a") }
        };
    }
}