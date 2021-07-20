package com.github.mizool.core.password;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.configuration.Config;

/**
 * @deprecated For newly saved passwords, use {@link Pbkdf2WithHmacSha512Hasher} instead. This hasher is kept to allow continued verification of old passwords.
 */
@Slf4j
@Deprecated
@MetaInfServices(PasswordHasher.class)
public class Pbkdf2WithHmacSha1Hasher extends AbstractPbkdf2WithHmacShaHasher
{
    public static final String ALGORITHM_NAME = "PBKDF2WithHmacSHA1";

    private static final int ITERATIONS_FOR_NEW_PASSWORDS = Config.systemProperties()
        .child(Pbkdf2WithHmacSha1Hasher.class.getName())
        .child("iterations")
        .intValue()
        .read()
        .orElse(65536);

    @Override
    int getIterationsForNewPasswords()
    {
        return ITERATIONS_FOR_NEW_PASSWORDS;
    }

    @Override
    public String getAlgorithmName()
    {
        return ALGORITHM_NAME;
    }
}
