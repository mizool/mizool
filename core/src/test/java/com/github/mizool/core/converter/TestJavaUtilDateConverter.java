package com.github.mizool.core.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestJavaUtilDateConverter
{
    private JavaUtilDateConverter converter;

    @BeforeMethod
    public void setUp()
    {
        converter = new JavaUtilDateConverter();
    }

    @Test(dataProvider = "timestamps")
    public void testZonedDateTimeConverter(String timestamps, String timestampString, String isoInstantString)
    {
        ZonedDateTime timestamp = ZonedDateTime.parse(timestampString, DateTimeFormatter.ISO_ZONED_DATE_TIME);

        Date dateResult = converter.fromZonedDateTime(timestamp);
        Instant expectedInstant = Instant.parse(isoInstantString);
        assertThat(expectedInstant).isEqualTo(dateResult.toInstant());

        ZonedDateTime timestampResult = converter.toZonedDateTime(dateResult);
        assertThat(timestampResult.isEqual(timestamp)).isTrue();
    }

    @DataProvider
    private Object[][] timestamps()
    {
        return new Object[][]{
            { "Berlin timestamp", "2017-01-13T09:00:00.000+01:00[Europe/Berlin]", "2017-01-13T08:00:00.000Z" },
            { "UTC timestamp", "2017-01-13T09:00:00.000+00:00[UTC]", "2017-01-13T09:00:00.000Z" },
            { "Sao Paulo timestamp", "2017-01-13T09:00:00.000-02:00[America/Sao_Paulo]", "2017-01-13T11:00:00.000Z" }
        };
    }
}
