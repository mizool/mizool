package com.github.mizool.core.password;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

@Slf4j
public abstract class Pbkdf2WithHmacShaHasher implements PasswordHasher
{
    @Data
    static class Digest
    {
        private static final String SEPARATOR = ":";

        private final int iterations;
        private final byte[] salt;
        private final byte[] hash;

        public static Pbkdf2WithHmacShaHasher.Digest valueOf(String digest)
        {
            List<String> parts = Splitter.on(SEPARATOR).splitToList(digest);
            int iterations = Integer.parseInt(parts.get(0));
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] salt = decoder.decode(parts.get(1));
            byte[] hash = decoder.decode(parts.get(2));
            return new Pbkdf2WithHmacShaHasher.Digest(iterations, salt, hash);
        }

        @Override
        public String toString()
        {
            Base64.Encoder encoder = Base64.getEncoder();
            return Joiner.on(SEPARATOR).join(iterations, encoder.encodeToString(salt), encoder.encodeToString(hash));
        }
    }

    private static final int PBE_KEY_LENGTH = 64 * 8;

    byte[] generateSalt()
    {
        Random random;
        try
        {
            random = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new CodeInconsistencyException(e);
        }
        byte[] result = new byte[16];
        random.nextBytes(result);
        return result;
    }

    Pbkdf2WithHmacShaHasher.Digest calculateDigest(
        char[] plaintextPassword, byte[] salt, int iterations, String algorithmName)
    {
        byte[] hash = calculateHash(plaintextPassword, salt, iterations, algorithmName);
        return new Pbkdf2WithHmacShaHasher.Digest(iterations, salt, hash);
    }

    private byte[] calculateHash(char[] plaintextPassword, byte[] salt, int iterations, String algorithmName)
    {
        KeySpec keySpec = new PBEKeySpec(plaintextPassword, salt, iterations, PBE_KEY_LENGTH);
        byte[] result;
        try
        {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithmName);
            long startTime = System.currentTimeMillis();
            result = secretKeyFactory.generateSecret(keySpec).getEncoded();
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.debug("Hashing with {} iterations took {}ms", iterations, elapsedTime);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            throw new CodeInconsistencyException(e);
        }
        return result;
    }

    abstract int getIterationsForNewPasswords();

    public boolean isResponsibleFor(String algorithm)
    {
        return algorithm.equals(getAlgorithmName());
    }

    public String hashPassword(char[] plaintextPassword)
    {
        byte[] salt = generateSalt();
        Digest digest = calculateDigest(plaintextPassword, salt, getIterationsForNewPasswords(), getAlgorithmName());
        return digest.toString();
    }

    public boolean passwordsMatch(char[] submittedPlaintext, String digest)
    {
        Pbkdf2WithHmacShaHasher.Digest knownDigest = Pbkdf2WithHmacShaHasher.Digest.valueOf(digest);
        Pbkdf2WithHmacShaHasher.Digest submittedDigest = calculateDigest(submittedPlaintext,
            knownDigest.getSalt(),
            knownDigest.getIterations(),
            getAlgorithmName());
        return submittedDigest.equals(knownDigest);
    }
}