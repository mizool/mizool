package com.github.mizool.core.validation;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestUuidValueAnnotation
{
    @RequiredArgsConstructor
    private static class TestData
    {
        @UuidValue(mandatory = false)
        private final String uuid;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @UuidValue(mandatory = false)
        private final List<String> uuids;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @UuidValue(mandatory = false)
        private final List<Integer> uuids;
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{ { null }, { "ddae0767-6dbc-4d6f-8fbe-2ba9ffd55e4a" } };
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{ { "" }, { "MOEP" } };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { null },
            { ImmutableList.of("ddae0767-6dbc-4d6f-8fbe-2ba9ffd55e4a") },
            { ImmutableList.of("ddae0767-6dbc-4d6f-8fbe-2ba9ffd55e4a", "abc12370-0000-aaaa-bbbb-2ba9ffd55e4a") }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("ddae0767-6dbc-4d6f-8fbe-2ba9ffd55e4a", "MOEP") }
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
        BeanValidation.assertUnacceptableValue(new TestData(value), UuidValue.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), UuidValue.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            UuidValue.class);
    }
}
