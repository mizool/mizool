package com.github.mizool.technology.gson.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import javax.ws.rs.ext.ParamConverter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestZonedDateTimeParamConverterProvider
{
    private ZonedDateTimeParamConverterProvider zonedDateTimeParamConverterProvider;

    @BeforeMethod
    public void setUp()
    {
        zonedDateTimeParamConverterProvider = new ZonedDateTimeParamConverterProvider();
    }

    @Test
    public void testProvidesConverterForDateTime()
    {
        ParamConverter<ZonedDateTime> converter = zonedDateTimeParamConverterProvider.getConverter(ZonedDateTime.class,
            null,
            null);

        assertThat(converter).isInstanceOf(ZonedDateTimeParamConverter.class);
    }

    @Test
    public void testReturnsNullForNonDateTime()
    {
        ParamConverter<String> converter = zonedDateTimeParamConverterProvider.getConverter(String.class, null, null);

        assertThat(converter).isNull();
    }
}