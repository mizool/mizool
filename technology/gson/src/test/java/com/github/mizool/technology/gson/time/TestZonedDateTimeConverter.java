package com.github.mizool.technology.gson.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.BadRequestException;

public class TestZonedDateTimeConverter
{
    private ZonedDateTimeConverter zonedDateTimeConverter;

    @BeforeMethod
    public void setUp()
    {
        zonedDateTimeConverter = new ZonedDateTimeConverter();
    }

    @DataProvider
    private Object[][] validDeserializationVariants()
    {
        return new Object[][]{
            {
                "zulu", "2018-01-01T00:00:00Z", ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "zulu with partial milliseconds",
                "2018-01-01T00:00:00.01Z",
                ZonedDateTime.parse("2018-01-01T00:00:00.010Z")
            }, {
                "zulu with milliseconds", "2018-01-01T00:00:00.001Z", ZonedDateTime.parse("2018-01-01T00:00:00.001Z")
            }, {
                "zulu with microseconds",
                "2018-01-01T00:00:00.000001Z",
                ZonedDateTime.parse("2018-01-01T00:00:00.000001Z")
            }, {
                "zulu with nanoseconds",
                "2018-01-01T00:00:00.000000001Z",
                ZonedDateTime.parse("2018-01-01T00:00:00.000000001Z")
            }, {
                "zulu with zero milliseconds", "2018-01-01T00:00:00.000Z", ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "zulu with zero microseconds",
                "2018-01-01T00:00:00.000000Z",
                ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "zulu with zero nanoseconds",
                "2018-01-01T00:00:00.000000000Z",
                ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "zero offset", "2018-01-01T00:00:00+00:00", ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "zero offset without colon", "2018-01-01T00:00:00+0000", ZonedDateTime.parse("2018-01-01T00:00:00Z")
            }, {
                "offset", "2018-01-01T00:00:00+01:00", ZonedDateTime.parse("2018-01-01T00:00:00+01:00")
            }, {
                "offset without colon", "2018-01-01T00:00:00+0100", ZonedDateTime.parse("2018-01-01T00:00:00+01:00")
            }, {
                "offset with milliseconds",
                "2018-01-01T00:00:00.001+01:00",
                ZonedDateTime.parse("2018-01-01T00:00:00.001+01:00")
            }, {
                "offset with milliseconds without colon",
                "2018-01-01T00:00:00.001+0100",
                ZonedDateTime.parse("2018-01-01T00:00:00.001+01:00")
            }, {
                "offset with zero milliseconds",
                "2018-01-01T00:00:00.000+01:00",
                ZonedDateTime.parse("2018-01-01T00:00:00+01:00")
            }, {
                "offset with zero milliseconds without colon",
                "2018-01-01T00:00:00.000+0100",
                ZonedDateTime.parse("2018-01-01T00:00:00+01:00")
            }
        };
    }

    @DataProvider
    private Object[][] invalidDeserializationVariants()
    {
        return new Object[][]{
            { "gibberish", "gibberish" },
            { "java specific zone suffix", "2018-01-01T00:00:00[Europe/Berlin]" },
            { "java specific zone suffix, despite valid offset", "2018-01-01T00:00:00+01:00[Europe/Berlin]" },
            { "neither offset nor zone", "2018-01-01T00:00:00" },
            { "invalid zone", "2018-01-01T00:00:00[invalid]" },
            { "invalid offset", "2018-01-01T00:00:00+99:99" }
        };
    }

    @DataProvider
    public Object[][] validSerializationVariants()
    {
        return new Object[][]{
            {
                "zulu", ZonedDateTime.parse("2018-01-01T00:00:00Z"), "2018-01-01T00:00:00.000Z"
            }, {
                "zulu with milliseconds", ZonedDateTime.parse("2018-01-01T00:00:00.001Z"), "2018-01-01T00:00:00.001Z"
            }, {
                "offset", ZonedDateTime.parse("2018-01-01T00:00:00+01:00"), "2018-01-01T00:00:00.000+01:00"
            }, {
                "offset with milliseconds",
                ZonedDateTime.parse("2018-01-01T00:00:00.001+01:00"),
                "2018-01-01T00:00:00.001+01:00"
            }
        };
    }

    @Test(dataProvider = "validDeserializationVariants")
    public void testValidDeserialization(String name, String string, ZonedDateTime expected)
    {
        ZonedDateTime actual = zonedDateTimeConverter.deserialize(string);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(dataProvider = "invalidDeserializationVariants", expectedExceptions = BadRequestException.class)
    public void testInvalidDeserialization(String name, String string)
    {
        zonedDateTimeConverter.deserialize(string);
    }

    @Test(dataProvider = "validSerializationVariants")
    public void testValidSerialization(String name, ZonedDateTime zonedDateTime, String expected)
    {
        String actual = zonedDateTimeConverter.serialize(zonedDateTime);
        assertThat(actual).isEqualTo(expected);
    }
}