package com.github.mizool.technology.jackson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.ws.rs.ext.ParamConverter;

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
