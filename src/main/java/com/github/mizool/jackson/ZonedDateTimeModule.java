package com.github.mizool.jackson;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ZonedDateTimeModule extends SimpleModule
{
    private static final String NAME = "ZonedDateTimeModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil()
    {
    };

    public ZonedDateTimeModule()
    {
        super(NAME, VERSION_UTIL.version());
        addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
    }
}