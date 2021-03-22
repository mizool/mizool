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
package com.github.mizool.tool.password;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.mizool.core.password.PasswordHasher;
import com.github.mizool.core.password.ServiceLoaderInstanceImpl;

@Slf4j
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
                ServiceLoaderInstanceImpl<PasswordHasher>
                    instances
                    = new ServiceLoaderInstanceImpl<>(PasswordHasher.class);
                instances.forEach(hasher -> {
                    PasswordHashTool passwordHashTool = new PasswordHashTool(hasher, parameters.getPassword());

                    String digest = passwordHashTool.getDigest();
                    if (digest != null)
                    {
                        log.info("Algorithm: {}  Digest: {}", passwordHashTool.getAlgorithmName(), digest);
                    }
                });
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