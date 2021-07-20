package com.github.mizool.core.password;

public interface PasswordHasher
{
    boolean isResponsibleFor(String algorithm);

    String hashPassword(char[] plaintextPassword);

    String getAlgorithmName();

    boolean passwordsMatch(char[] submittedPlaintext, String digest);
}
