package com.github.mizool.core;

import java.io.Serializable;
import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.NullMarked;

import com.google.errorprone.annotations.Immutable;

@Immutable
@NullMarked
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Identifier<T> implements Serializable
{
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IdentifierBuilder<T>
    {
        private static final String
            CHARACTERS_FOR_RANDOM_IDENTIFIER_VALUE
            = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        private static final SecureRandom RANDOM = new SecureRandom();
        private static final int RANDOM_IDENTIFIER_VALUE_LENGTH = 20;

        @NonNull
        private final Class<T> pojoClass;

        public Identifier<T> random()
        {
            return new Identifier<>(pojoClass, randomIdentifierValue());
        }

        public Identifier<T> of(@NonNull String value)
        {
            if (value.isEmpty())
            {
                throw new IllegalArgumentException("Identifier value is empty");
            }
            return new Identifier<>(pojoClass, value);
        }

        private static String randomIdentifierValue()
        {
            StringBuilder result = new StringBuilder(RANDOM_IDENTIFIER_VALUE_LENGTH);
            for (int i = 0; i < RANDOM_IDENTIFIER_VALUE_LENGTH; i++)
            {
                int characterIndex = RANDOM.nextInt(CHARACTERS_FOR_RANDOM_IDENTIFIER_VALUE.length());
                result.append(CHARACTERS_FOR_RANDOM_IDENTIFIER_VALUE.charAt(characterIndex));
            }
            return result.toString();
        }
    }

    public static <T> IdentifierBuilder<T> forPojo(Class<T> pojoClass)
    {
        return new IdentifierBuilder<>(pojoClass);
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
