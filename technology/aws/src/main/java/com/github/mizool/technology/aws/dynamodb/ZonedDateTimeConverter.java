package com.github.mizool.technology.aws.dynamodb;

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
        return ZonedDateTime.parse(object, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}
