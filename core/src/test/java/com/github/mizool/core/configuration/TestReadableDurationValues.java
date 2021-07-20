package com.github.mizool.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestReadableDurationValues
{
    @Test(dataProvider = "values")
    public void testValues(String string, Duration expected)
    {
        assertThat(ReadableDurationValues.parse(string)).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] values()
    {
        return new Object[][]{
            new Object[]{ "878342001 nanos", Duration.ofNanos(878342001) },
            new Object[]{ "391931 micros", Duration.ofNanos(391931000) },
            new Object[]{ "200 millis", Duration.ofMillis(200) },
            new Object[]{ "30 seconds", Duration.ofSeconds(30) },
            new Object[]{ "5 minutes", Duration.ofMinutes(5) },
            new Object[]{ "1 hours", Duration.ofHours(1) },
            new Object[]{ "7 days", Duration.ofDays(7) }
        };
    }
}
