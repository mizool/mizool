/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import com.github.mizool.core.exception.DataInconsistencyException;
import com.github.mizool.core.exception.ObjectNotFoundException;
import com.github.mizool.core.exception.UnprocessableEntityException;
import com.google.common.base.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Optionals
{
    /**
     * Used when a user directly requests an object, resulting in an ObjectNotFoundException if it does not exist.
     */
    public static final <T> T unwrapUserRequestedObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
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
    public static final <T> T unwrapRequiredObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
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
    public static final <T> T unwrapUserMentionedObject(@NonNull Optional<T> wrapped, @NonNull Class<T> classOfT)
    {
        if (!wrapped.isPresent())
        {
            throw new UnprocessableEntityException(classOfT.getSimpleName() + " not found");
        }
        return wrapped.get();
    }
}