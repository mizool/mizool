/*
 * Copyright 2020-2021 incub8 Software Labs GmbH
 * Copyright 2020-2021 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.password;

import com.github.mizool.core.configuration.Config;
import com.github.mizool.core.configuration.PropertyNode;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

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
