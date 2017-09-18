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
package com.github.mizool.core.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class JavaUtilDateConverter
{
    public Date fromZonedDateTime(ZonedDateTime pojo)
    {
        Date record = null;

        if (pojo != null)
        {
            record = Date.from(Instant.from(pojo));
        }
        return record;
    }

    public ZonedDateTime toZonedDateTime(Date record)
    {
        ZonedDateTime pojo = null;

        if (record != null)
        {
            pojo = ZonedDateTime.ofInstant(record.toInstant(), ZoneId.of("UTC"));
        }
        return pojo;
    }

    public Date fromLocalDate(LocalDate pojo)
    {
        Date record = null;

        if (pojo != null)
        {
            record = Date.from(Instant.from(pojo.atStartOfDay(ZoneId.of("UTC"))));
        }
        return record;
    }

    public LocalDate toLocalDate(Date record)
    {
        LocalDate pojo = null;

        if (record != null)
        {
            pojo = LocalDate.from(record.toInstant().atZone(ZoneId.of("UTC")));
        }
        return pojo;
    }
}