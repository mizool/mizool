/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.converter;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;

import com.google.common.collect.ImmutableList;

public class EnumConverter
{
    public String fromPojo(Enum<?> enumValue)
    {
        String result = null;

        if (enumValue != null)
        {
            result = enumValue.name();
        }
        return result;
    }

    public <T extends Enum<T>> T toPojo(@NonNull Class<T> enumClass, String value)
    {
        T result = null;
        if (value != null)
        {
            result = Enum.valueOf(enumClass, value);
        }
        return result;
    }

    public List<String> fromPojos(List<Enum<?>> enumValues)
    {
        List<String> result = null;
        if (enumValues != null && !enumValues.isEmpty())
        {
            result = enumValues.stream().map(this::fromPojo).collect(ImmutableList.toImmutableList());
        }
        return result;
    }

    public <T extends Enum<T>> List<T> toPojos(@NonNull Class<T> enumClass, List<String> values)
    {
        List<T> result = null;
        if (values != null && !values.isEmpty())
        {
            result = values.stream()
                .map(value -> this.toPojo(enumClass, value))
                .collect(ImmutableList.toImmutableList());
        }
        return result;
    }
}