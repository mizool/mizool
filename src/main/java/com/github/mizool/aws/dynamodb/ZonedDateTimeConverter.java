/**
 *  Copyright 2017 incub8 Software Labs GmbH
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
package com.github.mizool.aws.dynamodb;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class ZonedDateTimeConverter implements DynamoDBTypeConverter<String, ZonedDateTime>
{
    @Override
    public String convert(ZonedDateTime object)
    {
        return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(object);
    }

    @Override
    public ZonedDateTime unconvert(String object)
    {
        return ZonedDateTime.from(DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(object));
    }
}