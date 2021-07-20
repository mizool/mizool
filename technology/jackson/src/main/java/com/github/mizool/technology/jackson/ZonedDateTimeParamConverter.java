package com.github.mizool.technology.jackson;

import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;

import lombok.RequiredArgsConstructor;

import com.github.mizool.core.converter.ZonedDateTimeConverter;

@RequiredArgsConstructor
class ZonedDateTimeParamConverter implements ParamConverter<ZonedDateTime>
{
    private final ZonedDateTimeConverter zonedDateTimeConverter;

    @Override
    public ZonedDateTime fromString(String value)
    {
        return zonedDateTimeConverter.fromString(value);
    }

    @Override
    public String toString(ZonedDateTime value)
    {
        return zonedDateTimeConverter.toString(value);
    }
}
