/*
 *  Copyright 2017-2018 incub8 Software Labs GmbH
 *  Copyright 2017-2018 protel Hotelsoftware GmbH
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
import com.github.mizool.core.MetaInfServices;
import com.github.mizool.core.Streams;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.collect.ImmutableList;

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
                PasswordHashTool passwordHashTool = new PasswordHashTool(getDefaultHasher(), parameters.getPassword());

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

    private static PasswordHasher getDefaultHasher()
    {
        Iterable<PasswordHasher> hasherInstances = MetaInfServices.instances(PasswordHasher.class);
        ImmutableList<PasswordHasher> defaultPasswordHashers = Streams.sequential(hasherInstances)
            .collect(ImmutableList.toImmutableList());
        if (defaultPasswordHashers.isEmpty())
        {
            throw new CodeInconsistencyException(
                "No hasher instances for PasswordHasher, one should be registered as default");
        }
        if (defaultPasswordHashers.size() > 1)
        {
            throw new CodeInconsistencyException(
                "There are several hasher instances for PasswordHasher, only one should be registered as default");
        }
        return defaultPasswordHashers.stream().findFirst().get();
    }

    private String getDigest()
    {
        return passwordHasher.hashPassword(plainTextPassword);
    }

    private String getAlgorithmName()
    {
        return passwordHasher.getAlgorithmName();
    }
}