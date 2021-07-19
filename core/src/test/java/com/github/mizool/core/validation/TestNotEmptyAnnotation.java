package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

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
        return new Object[][]{
            { "blah" }, { "moep" }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { null }, { "" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("blah") }, { ImmutableList.of("blah", "moep") }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { null }, { ImmutableList.of("") }, { ImmutableList.of("blah", "") }
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
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), NotEmpty.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(values), NotEmpty.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)), NotEmpty.class);
    }
}
