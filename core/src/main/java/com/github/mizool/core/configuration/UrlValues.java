/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.configuration;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.experimental.UtilityClass;

@UtilityClass
class UrlValues
{
    public URL parse(String value)
    {
        return parse(null, value);
    }

    public URL parse(URL context, String value)
    {
        try
        {
            return new URL(context, value);
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
