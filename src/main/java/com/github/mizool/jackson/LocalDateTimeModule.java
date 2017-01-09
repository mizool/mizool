package com.github.mizool.jackson;

import java.time.LocalDateTime;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class LocalDateTimeModule extends SimpleModule
{
    private static final String NAME = "LocalDateTimeModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil()
    {
    };

    public LocalDateTimeModule()
    {
        super(NAME, VERSION_UTIL.version());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
    }
}