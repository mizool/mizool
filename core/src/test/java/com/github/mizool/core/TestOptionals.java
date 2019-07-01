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
import java.util.stream.Collectors;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestOptionals
{
    @Test(dataProvider = "optionalStreams")
    public <T> void testMapToValue(Collection<Optional<T>> input, Iterable<T> expected)
    {
        List<T> actual = input.stream().flatMap(Optionals::mapToValue).collect(Collectors.toList());
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DataProvider
    private Object[][] optionalStreams()
    {
        List<Object> emptyList = Collections.emptyList();

        Optional<Object> empty = Optional.empty();
        Optional<String> a = Optional.of("a");
        Optional<String> b = Optional.of("b");
        Optional<String> c = Optional.of("c");

        return new Object[][]{
            { emptyList, emptyList },
            { ImmutableList.of(empty), emptyList },
            { ImmutableList.of(a), ImmutableList.of("a") },
            { ImmutableList.of(a, b, c), ImmutableList.of("a", "b", "c") },
            { ImmutableList.of(a, empty, c), ImmutableList.of("a", "c") }
        };
    }
}