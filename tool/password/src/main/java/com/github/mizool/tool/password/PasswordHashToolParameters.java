/*
 * Copyright 2017-2021 incub8 Software Labs GmbH
 * Copyright 2017-2021 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.tool.password;

import java.util.List;

import lombok.Getter;

import com.beust.jcommander.Parameter;

class PasswordHashToolParameters
{
    @Parameter(names = "-help", description = "Displays this help screen", help = true)
    @Getter
    private boolean help;

    @Parameter(description = "password", required = true)
    private List<String> password;

    public char[] getPassword()
    {
        return password.get(0)
            .toCharArray();
    }
}
