package com.github.mizool.core.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Strings;

public class ZonedDateTimeConverter
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public ZonedDateTime fromString(String value)
    {
        ZonedDateTime result = null;
        if (!Strings.isNullOrEmpty(value))
        {
            result = ZonedDateTime.parse(value, FORMATTER);
        }
        return result;
    }

    public String toString(ZonedDateTime value)
    {
        String result = null;
        if (value != null)
        {
            result = FORMATTER.format(value);
        }
        return result;
    }

    public ZonedDateTime fromInstant(Instant value)
    {
        return fromInstant(value, ZoneOffset.UTC);
    }

    public ZonedDateTime fromInstant(Instant value, ZoneId zoneId)
    {
        ZonedDateTime result = null;
        if (value != null)
        {
            result = value.atZone(zoneId);
        }
        return result;
    }

    public Instant toInstant(ZonedDateTime value)
    {
        Instant result = null;
        if (value != null)
        {
            result = value.toInstant();
        }
        return result;
    }

    public ZonedDateTime fromLocalDateTime(LocalDateTime value)
    {
        return fromLocalDateTime(value, ZoneOffset.UTC);
    }

    public ZonedDateTime fromLocalDateTime(LocalDateTime value, ZoneId zoneId)
    {
        ZonedDateTime result = null;
        if (value != null)
        {
            result = value.atZone(zoneId);
        }
        return result;
    }

    public LocalDateTime toLocalDateTime(ZonedDateTime value)
    {
        LocalDateTime result = null;
        if (value != null)
        {
            result = value.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        }
        return result;
    }
}
