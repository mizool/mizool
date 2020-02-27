/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
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
package com.github.mizool.core.converter;

import java.time.Duration;

public class DurationConverter
{
    public String fromPojo(Duration value)
    {
        String result = null;
        if (value != null)
        {
            result = value.toString();
        }
        return result;
    }

    /**
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed to a duration
     */
    public Duration toPojo(String value)
    {
        Duration result = null;
        if (value != null)
        {
            result = Duration.parse(value);
        }
        return result;
    }
}