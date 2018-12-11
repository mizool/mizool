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

import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;

import lombok.RequiredArgsConstructor;

import com.github.mizool.core.converter.ZonedDateTimeConverter;

@RequiredArgsConstructor
class ZonedDateTimeParamConverter implements ParamConverter<ZonedDateTime>
{
    private final ZonedDateTimeConverter zonedDateTimeConverter;

    @Override
    public ZonedDateTime fromString(String value)
    {
        return zonedDateTimeConverter.fromString(value);
    }

    @Override
    public String toString(ZonedDateTime value)
    {
        return zonedDateTimeConverter.toString(value);
    }
}