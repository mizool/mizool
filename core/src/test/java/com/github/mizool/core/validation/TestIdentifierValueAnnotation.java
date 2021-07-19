package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestIdentifierValueAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @IdentifierValue(of = String.class, mandatory = false)
        private String identifier;
    }

    @AllArgsConstructor
    private final class TestListData
    {
        @IdentifierValue(of = String.class, mandatory = false)
        private List<String> identifiers;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @IdentifierValue(of = String.class, mandatory = false)
        private final List<Integer> identifiers;
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{ { null }, { "FOO" }, { "BAR" } };
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{ { "" } };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{ { ImmutableList.of("FOO") }, { ImmutableList.of("FOO", "BAR") } };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("FOO", "") }
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
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), IdentifierValue.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(values), IdentifierValue.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            IdentifierValue.class);
    }
}
