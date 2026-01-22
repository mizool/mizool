package com.github.mizool.core.rest.time;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestDateTimeFormat
{
    public static final DateTimeFormatter DESERIALIZATION = new DateTimeFormatterBuilder().parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .optionalStart()
        .appendOffset("+HH:MM:ss", "Z")
        .optionalEnd()
        .optionalStart()
        .appendOffset("+HHMMss", "Z")
        .optionalEnd()
        .toFormatter();
    public static final DateTimeFormatter SERIALIZATION = new DateTimeFormatterBuilder().parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .optionalStart()
        .appendFraction(NANO_OF_SECOND, 3, 3, true)
        .appendOffsetId()
        .toFormatter();
}