package com.github.mizool.technology.gson.time;

import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;

public class ZonedDateTimeParamConverter implements ParamConverter<ZonedDateTime>
{
    @Override
    public ZonedDateTime fromString(String value)
    {
        return new ZonedDateTimeConverter().deserialize(value);
    }

    @Override
    public String toString(ZonedDateTime value)
    {
        return new ZonedDateTimeConverter().serialize(value);
    }
}