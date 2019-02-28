package com.github.mizool.technology.typemapping.core.jdbc;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.google.common.annotations.VisibleForTesting;

@RequiredArgsConstructor(onConstructor = @__({ @Inject, @VisibleForTesting }))
public class TimeZoneNormalizer
{
    public ZonedDateTime normalize(ZoneId zoneId, ZonedDateTime timestamp)
    {
        return timestamp.withZoneSameInstant(zoneId);
    }
}