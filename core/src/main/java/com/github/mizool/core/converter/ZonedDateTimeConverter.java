package com.github.mizool.core.converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class ZonedDateTimeConverter
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static ZonedDateTime fromString(String value)
    {
        ZonedDateTime result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = ZonedDateTime.from(FORMATTER.parse(value));
        }
        return result;
    }

    public static String toString(ZonedDateTime value)
    {
        String result = null;
        if (value != null)
        {
            result = FORMATTER.format(value);
        }
        return result;
    }
}