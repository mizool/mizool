package com.github.mizool.core.validation;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestEnumValueAnnotation
{
    private static enum TestEnum
    {
        FOO,
        BAR
    }

    @RequiredArgsConstructor
    private static class TestData
    {
        @EnumValue(mandatory = false, value = TestEnum.class)
        private final String enumValue;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @EnumValue(mandatory = false, value = TestEnum.class)
        private final List<String> enumValues;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @EnumValue(mandatory = false, value = TestEnum.class)
        private final List<Integer> enumValues;
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{ { null }, { "FOO" } };
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{ { "" }, { "MOEP" } };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{ { null }, { ImmutableList.of("FOO") }, { ImmutableList.of("FOO", "BAR") } };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("MOEP") }, { ImmutableList.of("FOO", "MOEP") }
        };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValues(String value)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValues(String value)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), EnumValue.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(values), EnumValue.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            EnumValue.class);
    }
}
