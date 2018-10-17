/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.enterprise.inject.Instance;

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