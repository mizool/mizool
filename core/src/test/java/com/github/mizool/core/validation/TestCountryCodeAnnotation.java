package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestCountryCodeAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @CountryCode(mandatory = false)
        private String countryCode;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @CountryCode(mandatory = false)
        private final List<String> countryCodes;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @CountryCode(mandatory = false)
        private final List<Integer> countryCodes;
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{
            { null }, { "DE" }, { "US" }, { "AU" }, { "FR" }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "foo" }, { "germany" }, { "usa" }, { "france" }, { "EN" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{ { null }, { ImmutableList.of("DE") }, { ImmutableList.of("DE", "US", "AU", "FR") } };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("foo") }, { ImmutableList.of("DE", "germany") }
        };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValue(String value)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValue(String value)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), CountryCode.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(values), CountryCode.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            CountryCode.class);
    }
}
