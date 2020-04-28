/*
 * Copyright 2017-2020 incub8 Software Labs GmbH
 * Copyright 2017-2020 protel Hotelsoftware GmbH
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

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

@RequiredArgsConstructor
public class PasswordHashTool
{
    public static void main(String[] args)
    {
        PasswordHashToolParameters parameters = new PasswordHashToolParameters();
        JCommander jCommander = new JCommander(parameters);
        jCommander.setProgramName(PasswordHashTool.class.getSimpleName());
        try
        {
            jCommander.parse(args);

            if (parameters.isHelp())
            {
                jCommander.usage();
            }
            else
            {
                PasswordHashTool instance = new PasswordHashTool(parameters.getPassword());
                instance.printDigests();
            }
        }
        catch (ParameterException ignored)
        {
            jCommander.usage();
        }
    }

    private final char[] plainTextPassword;

    public void printDigests()
    {
        PasswordHasher[] hashers = { new Pbkdf2WithHmacSha1Hasher(), new Argon2Hasher() };
        for (PasswordHasher hasher : hashers)
        {
            String digest = hasher.hashPassword(plainTextPassword);
            System.out.println(MessageFormat.format("{0} digest: {1}", hasher.getClass().getSimpleName(), digest));
        }
    }
}