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
package com.github.mizool.password;

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

import com.github.mizool.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

@Slf4j
public class Pbkdf2WithHmacSha1Hasher implements PasswordHasher
{
    @Data
    private static class Digest
    {
        private static final String SEPARATOR = ":";

        private final int iterations;
        private final byte[] salt;
        private final byte[] hash;

        public static Digest valueOf(String digest)
        {
            List<String> parts = Splitter.on(SEPARATOR).splitToList(digest);
            int iterations = Integer.parseInt(parts.get(0));
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] salt = decoder.decode(parts.get(1));
            byte[] hash = decoder.decode(parts.get(2));
            return new Digest(iterations, salt, hash);
        }

        @Override
        public String toString()
        {
            Base64.Encoder encoder = Base64.getEncoder();
            return Joiner.on(SEPARATOR).join(iterations, encoder.encodeToString(salt), encoder.encodeToString(hash));
        }
    }

    public static final String ALGORITHM_NAME = "PBKDF2WithHmacSHA1";

    @VisibleForTesting
    protected static final String ITERATIONS_PROPERTY_NAME = Pbkdf2WithHmacSha1Hasher.class.getName() + ".iterations";

    private static final int
        ITERATIONS_FOR_NEW_PASSWORDS
        = Integer.parseInt(System.getProperty(ITERATIONS_PROPERTY_NAME, "65536"));
    private static final int PBE_KEY_LENGTH = 64 * 8;

    @Override
    public boolean isResponsibleFor(String algorithm)
    {
        return algorithm.equals(ALGORITHM_NAME);
    }

    @Override
    public String hashPassword(char[] plaintextPassword)
    {
        byte[] salt = generateSalt();
        Digest digest = calculateDigest(plaintextPassword, salt, ITERATIONS_FOR_NEW_PASSWORDS);
        return digest.toString();
    }

    private byte[] generateSalt()
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

    @Override
    public boolean passwordsMatch(char[] submittedPlaintext, String digest)
    {
        Digest knownDigest = Digest.valueOf(digest);
        Digest submittedDigest = calculateDigest(
            submittedPlaintext, knownDigest.getSalt(), knownDigest.getIterations());
        return submittedDigest.equals(knownDigest);
    }

    private Digest calculateDigest(char[] plaintextPassword, byte[] salt, int iterations)
    {
        byte[] hash = calculateHash(plaintextPassword, salt, iterations);
        return new Digest(iterations, salt, hash);
    }

    private byte[] calculateHash(char[] plaintextPassword, byte[] salt, int iterations)
    {
        KeySpec keySpec = new PBEKeySpec(plaintextPassword, salt, iterations, PBE_KEY_LENGTH);
        byte[] result;
        try
        {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_NAME);
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
}