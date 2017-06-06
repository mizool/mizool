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
package com.github.mizool.core.password;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.google.common.collect.Iterables;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class PasswordHasherRegistry
{
    private final Instance<PasswordHasher> passwordHashers;

    public PasswordHasher getHasher(String algorithm)
    {
        return Iterables.getOnlyElement(
            Iterables.filter(
                passwordHashers, passwordHasher -> passwordHasher.isResponsibleFor(algorithm)));
    }
}