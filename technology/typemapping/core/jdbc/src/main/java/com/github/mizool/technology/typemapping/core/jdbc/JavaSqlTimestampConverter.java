package com.github.mizool.technology.typemapping.core.jdbc;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class JavaSqlTimestampConverter
{
    private final TimeZoneNormalizer timeZoneNormalizer;

    public Timestamp fromPojo(ZonedDateTime pojo)
    {
        Timestamp record = null;

        if (pojo != null)
        {
            record = new Timestamp(pojo.toInstant().toEpochMilli());
        }

        return record;
    }

    public ZonedDateTime toPojo(ZoneId zoneId, Timestamp record)
    {
        ZonedDateTime pojo = null;

        if (record != null)
        {
            pojo = timeZoneNormalizer.normalize(zoneId, Instant.ofEpochMilli(record.getTime()).atZone(ZoneOffset.UTC));
        }

        return pojo;
    }
}