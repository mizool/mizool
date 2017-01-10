package com.github.mizool.jackson;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>
{
    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        String zonedDateTimeString = parser.getValueAsString();
        return ZonedDateTime.from(DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(zonedDateTimeString));
    }
}