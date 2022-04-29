package com.github.mizool.core.validation;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestTimeZoneValueAnnotation
{
    @RequiredArgsConstructor
    private static class TestData
    {
        @TimeZoneValue(mandatory = false)
        private final String timeZoneValue;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @TimeZoneValue(mandatory = false)
        private final List<String> timeZoneValues;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @TimeZoneValue(mandatory = false)
        private final List<Integer> timeZoneValues;
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{
            { null }, { "Europe/Paris" }, { "Europe/Berlin" }, { "Asia/Tokyo" }, { "UTC" }, { "GMT" }
        };
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{
            { "" }, { "UT" }, { "+2:00" }, { "GMT+2" }, { "UTC-01:00" }, { "Europe/Dortmund" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { null }, { ImmutableList.of("Europe/Paris") }, { ImmutableList.of("Asia/Tokyo", "UTC") }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("Asia/Tokyo", "+2:00") }
        };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValues(String value)
    {
        BeanValidation.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValues(String value)
    {
        BeanValidation.assertUnacceptableValue(new TestData(value), TimeZoneValue.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), TimeZoneValue.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            TimeZoneValue.class);
    }
}
