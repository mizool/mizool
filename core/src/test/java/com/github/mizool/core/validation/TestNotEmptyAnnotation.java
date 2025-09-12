package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestNotEmptyAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @NotEmpty(mandatory = true)
        private String content;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @NotEmpty(mandatory = true)
        private final List<String> contents;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @NotEmpty(mandatory = true)
        private final List<Integer> contents;
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{ { "blah" }, { "moep" } };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{ { null }, { "" } };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{ { List.of("blah") }, { List.of("blah", "moep") } };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{ { null }, { List.of("") }, { List.of("blah", "") } };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValue(String value)
    {
        BeanValidation.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValue(String value)
    {
        BeanValidation.assertUnacceptableValue(new TestData(value), NotEmpty.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), NotEmpty.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(List.of(1, 5)), NotEmpty.class);
    }
}
