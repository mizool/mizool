package com.github.mizool.technology.gson.time;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import com.github.mizool.core.exception.BadRequestException;
import com.github.mizool.core.rest.time.RestDateTimeFormat;

public class ZonedDateTimeConverter
{
    public ZonedDateTime deserialize(String isoString)
    {
        try
        {
            return ZonedDateTime.parse(isoString, RestDateTimeFormat.DESERIALIZATION);
        }
        catch (DateTimeParseException e)
        {
            throw new BadRequestException("Could not deserialize", e);
        }
    }

    public String serialize(ZonedDateTime zonedDateTime)
    {
        return RestDateTimeFormat.SERIALIZATION.format(zonedDateTime);
    }
}