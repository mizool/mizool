package com.github.mizool.technology.rest.time;

import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestLocalDateFormat
{
    public static final DateTimeFormatter DESERIALIZATION = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter SERIALIZATION = DateTimeFormatter.ISO_LOCAL_DATE;
}