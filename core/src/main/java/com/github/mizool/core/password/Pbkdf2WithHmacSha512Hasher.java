package com.github.mizool.core.password;

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.configuration.Config;

@Slf4j
@MetaInfServices(PasswordHasher.class)
public class Pbkdf2WithHmacSha512Hasher extends AbstractPbkdf2WithHmacShaHasher
{
    public static final String ALGORITHM_NAME = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS_FOR_NEW_PASSWORDS = Config.systemProperties()
        .child(Pbkdf2WithHmacSha512Hasher.class.getName())
        .child("iterations")
        .intValue()
        .read()
        .orElse(122_000);

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
