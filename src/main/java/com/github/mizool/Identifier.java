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
package com.github.mizool;

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
    private static final SecureRandom rnd = new SecureRandom();

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BasicIdentifierBuilder<T>
    {
        private final Class<T> pojoClass;

        public Identifier<T> random()
        {
            return new Identifier<T>(pojoClass, randomString(20));
        }

        public Identifier<T> of(@NonNull String value)
        {
            return new Identifier<T>(pojoClass, value);
        }
    }

    private static String randomString(int len)
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            sb.append(CHARACTERS_FOR_RANDOM_STRING.charAt(rnd.nextInt(CHARACTERS_FOR_RANDOM_STRING.length())));
        }
        return sb.toString();
    }

    public static <T> BasicIdentifierBuilder<T> forPojo(Class<T> pojoClass)
    {
        return new BasicIdentifierBuilder<T>(pojoClass);
    }

    private final Class<T> pojoClass;
    private final String id;

    public String getValue()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return pojoClass.getSimpleName() + ":" + getValue();
    }
}