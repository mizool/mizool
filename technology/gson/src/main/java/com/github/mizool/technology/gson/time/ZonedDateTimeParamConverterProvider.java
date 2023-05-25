package com.github.mizool.technology.gson.time;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ZonedDateTimeParamConverterProvider implements ParamConverterProvider
{
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        if (rawType.equals(ZonedDateTime.class))
        {
            return getParamConverter();
        }
        else
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ParamConverter<T> getParamConverter()
    {
        return (ParamConverter<T>) new ZonedDateTimeParamConverter();
    }
}