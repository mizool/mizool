/*
 * Copyright 2017-2019 incub8 Software Labs GmbH
 * Copyright 2017-2019 protel Hotelsoftware GmbH
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

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.DataInconsistencyException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.UnprocessableEntityException;

@UtilityClass
public class Optionals
{
    /**
     * Used when a user directly requests an object, resulting in an ObjectNotFoundException if it does not exist.
     */
    public <T> T unwrapUserRequestedObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        return wrapped.orElseThrow(() -> new ObjectNotFoundException(classOfT.getSimpleName() + " not found"));
    }

    /**
     * Used when a user directly requests an object, resulting in an ObjectNotFoundException if it does not exist.
     */
    public <T> Function<Optional<T>, T> unwrapUserRequestedObject(@NonNull Class<T> classOfT)
    {
        return optional -> unwrapUserRequestedObject(optional, classOfT);
    }

    /**
     * Used when a user directly requests an object, resulting in an ObjectNotFoundException if it does not exist.
     *
     * @deprecated Use {@link Optionals#unwrapUserRequestedObject(Optional, Class)} instead.
     */
    @Deprecated
    public <T> T unwrapUserRequestedObject(
        @NonNull com.google.common.base.Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new ObjectNotFoundException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }

    /**
     * Used when an object can be reasonably expected to exist, resulting in a DataInconsistencyException if it does not
     * exist.
     */
    public <T> T unwrapRequiredObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        return wrapped.orElseThrow(() -> new DataInconsistencyException(classOfT.getSimpleName() + " not found"));
    }

    /**
     * Used when an object can be reasonably expected to exist, resulting in a DataInconsistencyException if it does not
     * exist.
     */
    public <T> Function<Optional<T>, T> unwrapRequiredObject(@NonNull Class<T> classOfT)
    {
        return optional -> unwrapRequiredObject(optional, classOfT);
    }

    /**
     * Used when an object can be reasonably expected to exist, resulting in a DataInconsistencyException if it does not
     * exist.
     *
     * @deprecated Use {@link Optionals#unwrapRequiredObject(Optional, Class)} instead.
     */
    @Deprecated
    public <T> T unwrapRequiredObject(@NonNull com.google.common.base.Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new DataInconsistencyException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }

    /**
     * Used when a user-submitted entity refers to another object, resulting in a UnprocessableEntityException if that
     * object does not exist.
     */
    public <T> T unwrapUserMentionedObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        return wrapped.orElseThrow(() -> new UnprocessableEntityException(classOfT.getSimpleName() + " not found"));
    }

    /**
     * Used when a user-submitted entity refers to another object, resulting in a UnprocessableEntityException if that
     * object does not exist.
     */
    public <T> Function<Optional<T>, T> unwrapUserMentionedObject(@NonNull Class<T> classOfT)
    {
        return optional -> unwrapUserMentionedObject(optional, classOfT);
    }

    /**
     * Used when a user-submitted entity refers to another object, resulting in a UnprocessableEntityException if that
     * object does not exist.
     *
     * @deprecated Use {@link Optionals#unwrapUserMentionedObject(Optional, Class)} instead.
     */
    @Deprecated
    public <T> T unwrapUserMentionedObject(
        @NonNull com.google.common.base.Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new UnprocessableEntityException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }

    /**
     * Used in streams to {@linkplain Stream#flatMap(Function) flat-map} each {@link Optional} to its value if
     * present.<br>
     * <br>
     * This method is intended to be used as follows:
     * <pre>{@code
     *     .flatMap(Optionals::streamPresentValue)
     * }</pre>
     * Using this method is equivalent of chaining {@link Optional#isPresent()} and {@link Optional#get()} like this:
     * <pre>{@code
     *     .filter(Optional::isPresent)
     *     .map(Optional::get)
     * }</pre>
     */
    public <T> Stream<T> streamPresentValue(@NonNull Optional<T> optional)
    {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}