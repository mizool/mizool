/*
 *  Copyright 2017-2021 incub8 Software Labs GmbH
 *  Copyright 2017-2021 protel Hotelsoftware GmbH
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
package com.github.mizool.core.password;

import lombok.RequiredArgsConstructor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

@RequiredArgsConstructor
public class PasswordHashTool
{
    public static void main(String[] args)
    {
        PasswordHashToolParameters parameters = new PasswordHashToolParameters();
        JCommander jCommander = new JCommander(parameters);
        jCommander.setProgramName(PasswordHashTool.class.getName());
        try
        {
            jCommander.parse(args);

            if (parameters.isHelp())
            {
                jCommander.usage();
            }
            else
            {
                PasswordHashTool passwordHashTool = new PasswordHashTool(PasswordHasherRegistry.getDefaultHasher(),
                    parameters.getPassword());

                String digest = passwordHashTool.getDigest();
                if (digest != null)
                {
                    System.out.println("Algorithm: " + passwordHashTool.getAlgorithmName() + " Digest: " + digest);
                }
            }
        }
        catch (ParameterException ignored)
        {
            jCommander.usage();
        }
    }

    private final PasswordHasher passwordHasher;
    private final char[] plainTextPassword;

    private String getDigest()
    {
        return passwordHasher.hashPassword(plainTextPassword);
    }

    private String getAlgorithmName()
    {
        return passwordHasher.getAlgorithmName();
    }
}