package com.github.mizool.technology.jackson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.ws.rs.ext.ParamConverter;

import com.google.common.base.Strings;

class LocalDateParamConverter implements ParamConverter<LocalDate>
{
    @Override
    public LocalDate fromString(String value)
    {
        LocalDate result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(value));
        }
        return result;
    }

    @Override
    public String toString(LocalDate value)
    {
        String result = null;
        if (value != null)
        {
            result = DateTimeFormatter.ISO_LOCAL_DATE.format(value);
        }
        return result;
    }
}
