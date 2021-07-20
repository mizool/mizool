package com.github.mizool.core.password;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.configuration.PropertyNode;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

@MetaInfServices
public class Argon2Hasher implements PasswordHasher
{
    public static final String ALGORITHM_NAME = "Argon2";

    private static final PropertyNode CONFIG = Config.systemProperties()
        .child(Argon2Hasher.class.getName());
    private static final int ITERATIONS_FOR_NEW_PASSWORDS = CONFIG.child("iterations")
        .intValue()
        .read()
        .orElse(10);
    private static final int MEMORY = CONFIG.child("memory")
        .intValue()
        .read()
        .orElse(65536);
    private static final int PARALLELISM = CONFIG.child("parallelism")
        .intValue()
        .read()
        .orElse(1);

    private final Argon2 argon2 = Argon2Factory.create();

    @Override
    public boolean isResponsibleFor(String algorithm)
    {
        return algorithm.equals(ALGORITHM_NAME);
    }

    /**
     * @param plaintextPassword this parameter should be wiped/cleaned in a try-finally block after usage
     */
    @Override
    public String hashPassword(char[] plaintextPassword)
    {
        return argon2.hash(ITERATIONS_FOR_NEW_PASSWORDS, MEMORY, PARALLELISM, plaintextPassword);
    }

    /**
     * @param submittedPlaintext this parameter should be wiped/cleaned in a try-finally block after usage
     */
    @Override
    public boolean passwordsMatch(char[] submittedPlaintext, String digest)
    {
        return argon2.verify(digest, submittedPlaintext);
    }

    @Override
    public String getAlgorithmName()
    {
        return ALGORITHM_NAME;
    }
}
