/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class ZonedDateTimeConverter
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public ZonedDateTime fromString(String value)
    {
        ZonedDateTime result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = ZonedDateTime.from(FORMATTER.parse(value));
        }
        return result;
    }

    public String toString(ZonedDateTime value)
    {
        String result = null;
        if (value != null)
        {
            result = FORMATTER.format(value);
        }
        return result;
    }

    public ZonedDateTime fromInstant(Instant value)
    {
        ZonedDateTime result = null;

        if (value != null)
        {
            result = ZonedDateTime.ofInstant(value, ZoneId.of("UTC"));
        }
        return result;
    }

    public Instant toInstant(ZonedDateTime value)
    {
        Instant result = null;

        if (value != null)
        {
            result = Instant.from(value);
        }
        return result;
    }
}