package com.github.mizool.technology.jackson;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;

import com.google.common.base.Strings;

class LocalTimeParamConverter implements ParamConverter<LocalTime>
{
    @Override
    public LocalTime fromString(String value)
    {
        LocalTime result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(value));
        }
        return result;
    }

    @Override
    public String toString(LocalTime value)
    {
        String result = null;
        if (value != null)
        {
            result = DateTimeFormatter.ISO_LOCAL_TIME.format(value);
        }
        return result;
    }
}
