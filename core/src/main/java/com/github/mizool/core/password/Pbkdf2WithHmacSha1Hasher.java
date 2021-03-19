/*
 * Copyright 2017-2021 incub8 Software Labs GmbH
 * Copyright 2017-2021 protel Hotelsoftware GmbH
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

import lombok.extern.slf4j.Slf4j;

import org.kohsuke.MetaInfServices;

import com.github.mizool.core.configuration.Config;

@Slf4j
@Deprecated
public class Pbkdf2WithHmacSha1Hasher extends Pbkdf2WithHmacShaHasher
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