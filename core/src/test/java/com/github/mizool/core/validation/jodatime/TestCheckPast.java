package com.github.mizool.core.validation.jodatime;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCheckPast
{
    private static final DateTime NOW = new DateTime(2016, 1, 1, 0, 0);

    private CheckPast checkPast;

    @BeforeMethod
    public void setUp()
    {
        checkPast = new CheckPast()
        {
            @Override
            protected DateTime now()
            {
                return NOW;
            }
        };
    }

    @Test(dataProvider = "dateTimeVariants")
    public void testsPast(String name, DateTime dateTime, boolean expected)
    {
        boolean actual = checkPast.isValid(dateTime, null);

        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] dateTimeVariants()
    {
        return new Object[][]{
            { "now", NOW, false }, { "future", NOW.plus(1), false }, { "past", NOW.minus(1), true }
        };
    }
}