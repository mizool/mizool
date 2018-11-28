/*
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
package com.github.mizool.technology.jackson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;

import com.google.common.base.Strings;

class LocalDateTimeParamConverter implements ParamConverter<LocalDateTime>
{
    @Override
    public LocalDateTime fromString(String value)
    {
        LocalDateTime result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value));
        }
        return result;
    }

    @Override
    public String toString(LocalDateTime value)
    {
        String result = null;
        if (value != null)
        {
            result = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value);
        }
        return result;
    }
}