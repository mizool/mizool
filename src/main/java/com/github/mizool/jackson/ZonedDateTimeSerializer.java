package com.github.mizool.jackson;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime>
{
    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        String zonedDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value);
        gen.writeString(zonedDateTime);
    }
}