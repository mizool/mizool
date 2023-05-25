package com.github.mizool.core.password;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.configuration.Config;
import com.google.common.collect.Iterables;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class PasswordHasherRegistry
{
    private static final String DEFAULT_HASHING_ALGORITHM = Config.systemProperties()
        .child(PasswordHasherRegistry.class.getName())
        .child("defaultAlgorithm")
        .stringValue()
        .read()
        .orElse(Pbkdf2WithHmacSha512Hasher.ALGORITHM_NAME);

    private final Instance<PasswordHasher> passwordHashers;

    public PasswordHasher getHasher(String algorithm)
    {
        return Iterables.getOnlyElement(Iterables.filter(passwordHashers,
            passwordHasher -> passwordHasher.isResponsibleFor(algorithm)));
    }

    public PasswordHasher getDefaultHasher()
    {
        return Iterables.getOnlyElement(Iterables.filter(passwordHashers,
            passwordHasher -> passwordHasher.getAlgorithmName()
                .equals(DEFAULT_HASHING_ALGORITHM)));
    }
}
