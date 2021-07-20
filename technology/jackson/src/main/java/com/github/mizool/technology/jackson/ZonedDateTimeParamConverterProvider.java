package com.github.mizool.technology.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

import javax.inject.Inject;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.github.mizool.core.converter.ZonedDateTimeConverter;

@Provider
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class ZonedDateTimeParamConverterProvider implements ParamConverterProvider
{
    private final ZonedDateTimeConverter zonedDateTimeConverter;

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        ParamConverter<T> result = null;

        if (rawType.equals(ZonedDateTime.class))
        {
            result = getParamConverter();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> ParamConverter<T> getParamConverter()
    {
        return (ParamConverter<T>) new ZonedDateTimeParamConverter(zonedDateTimeConverter);
    }
}
