package com.github.mizool.technology.jackson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;

@Provider
public class ZonedDateTimeParamConverterProvider implements ParamConverterProvider
{
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations)
    {
        ParamConverter<T> result = null;

        if (rawType.equals(ZonedDateTime.class))
        {
            result = new ParamConverter<T>()
            {
                @Override
                public T fromString(String value)
                {
                    ZonedDateTime result = null;
                    if (!Strings.isNullOrEmpty(value))
                    {
                        result = ZonedDateTime.from(DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(value));
                    }
                    return rawType.cast(result);
                }

                @Override
                public String toString(T value)
                {
                    String result = null;
                    if (value != null)
                    {
                        result = DateTimeFormatter.ISO_ZONED_DATE_TIME.format((ZonedDateTime) value);
                    }
                    return result;
                }
            };
        }
        return result;
    }
}
