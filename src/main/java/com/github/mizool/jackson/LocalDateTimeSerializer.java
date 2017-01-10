package com.github.mizool.jackson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>
{
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        String localDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value);
        gen.writeString(localDateTime);
    }
}