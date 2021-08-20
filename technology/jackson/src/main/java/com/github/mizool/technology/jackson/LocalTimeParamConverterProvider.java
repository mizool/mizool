package com.github.mizool.technology.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalTime;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

@Provider
public class LocalTimeParamConverterProvider implements ParamConverterProvider
{
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        ParamConverter<T> result = null;

        if (rawType.equals(LocalTime.class))
        {
            result = getParamConverter();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> ParamConverter<T> getParamConverter()
    {
        return (ParamConverter<T>) new LocalTimeParamConverter();
    }
}
