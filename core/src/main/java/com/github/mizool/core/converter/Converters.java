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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.GuavaCollectors;

@UtilityClass
public class Converters
{
    public <D, P> List<D> fromPojos(List<P> pojos, Function<P, D> converter)
    {
        List<D> dtos = null;
        if (pojos != null && !pojos.isEmpty())
        {
            dtos = pojos.stream().map(converter).collect(GuavaCollectors.toImmutableList());
        }
        return dtos;
    }

    public <D, P> List<P> toPojos(List<D> dtos, Function<D, P> converter)
    {
        List<P> pojos;
        if (dtos != null)
        {
            pojos = dtos.stream().map(converter).collect(GuavaCollectors.toImmutableList());
        }
        else
        {
            pojos = Collections.emptyList();
        }
        return pojos;
    }

    public <D, P> Set<D> fromPojos(Set<P> pojos, Function<P, D> converter)
    {
        Set<D> dtos = null;
        if (pojos != null && !pojos.isEmpty())
        {
            dtos = pojos.stream().map(converter).collect(GuavaCollectors.toImmutableSet());
        }

        return dtos;
    }

    public <D, P> Set<P> toPojos(Set<D> dtos, Function<D, P> converter)
    {
        Set<P> pojos;
        if (dtos != null)
        {
            pojos = dtos.stream().map(converter).collect(GuavaCollectors.toImmutableSet());
        }
        else
        {
            pojos = Collections.emptySet();
        }
        return pojos;
    }
}