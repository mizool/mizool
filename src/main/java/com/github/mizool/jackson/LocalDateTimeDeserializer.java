package com.github.mizool.jackson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>
{
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        String localDateTimeString = parser.getValueAsString();
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(localDateTimeString));
    }
}