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

import java.io.Serializable;
import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Identifier<T> implements Serializable
{
    private static final String
        CHARACTERS_FOR_RANDOM_STRING
        = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IdentifierBuilder<T>
    {
        @NonNull
        private final Class<T> pojoClass;

        public Identifier<T> random()
        {
            return new Identifier<T>(pojoClass, randomString(20));
        }

        public Identifier<T> of(@NonNull String value)
        {
            if (value.isEmpty())
            {
                throw new IllegalArgumentException("Identifier value is empty");
            }
            return new Identifier<T>(pojoClass, value);
        }
    }

    private static String randomString(int length)
    {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            int characterIndex = RANDOM.nextInt(CHARACTERS_FOR_RANDOM_STRING.length());
            result.append(CHARACTERS_FOR_RANDOM_STRING.charAt(characterIndex));
        }
        return result.toString();
    }

    public static <T> IdentifierBuilder<T> forPojo(Class<T> pojoClass)
    {
        return new IdentifierBuilder<T>(pojoClass);
    }

    @NonNull
    private final Class<T> pojoClass;
    @NonNull
    private final String value;

    @Override
    public String toString()
    {
        return pojoClass.getSimpleName() + ":" + getValue();
    }
}