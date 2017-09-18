package com.github.mizool.technology.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

@Provider
public class LocalDateTimeParamConverterProvider implements ParamConverterProvider
{
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        ParamConverter<T> result = null;

        if (rawType.equals(LocalDateTime.class))
        {
            result = new ParamConverter<T>()
            {
                @Override
                public T fromString(String value)
                {
                    LocalDateTime result = null;
                    if (!Strings.isNullOrEmpty(value))
                    {
                        result = LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(value));
                    }
                    return rawType.cast(result);
                }

                @Override
                public String toString(T value)
                {
                    String result = null;
                    if (value != null)
                    {
                        result = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime) value);
                    }
                    return result;
                }
            };
        }
        return result;
    }
}
