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
                throw new ReductionException("\"" + u.toString() + "\" is not equal to \"" + t.toString() + "\"");
            }
            return t;
        };
    }
}