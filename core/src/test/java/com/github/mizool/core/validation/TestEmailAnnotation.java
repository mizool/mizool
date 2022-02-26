package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestEmailAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @Email(mandatory = false)
        private String email;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @Email(mandatory = false)
        private final List<String> emails;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @Email(mandatory = false)
        private final List<Integer> emails;
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{
            { "b@b.de" },
            { "bob@example.com" },
            { null },
            { "whatever-something_IDONTCARE'\"\"yesindeed@something.travel" }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "b" },
            { "b@" },
            { "b@b" },
            { "b@b." },
            { ".b@b." },
            { "f@tc@t@spl@t.com.tw" },
            { "whitespace @example.org" },
            { " whitespace@example.org" },
            { "whitespace@example.org " },
            { "waytoolongemailaddresswaytoolongemailaddresswaytoolongemailaddresswaytoolongemailaddresswaytoolongemailaddress@example.org" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { null }, { ImmutableList.of("b@b.de") }, { ImmutableList.of("b@b.de", "bob@example.com") }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("b") }, { ImmutableList.of("bob@example.com", "b@") }
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
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), Email.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(values), Email.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)), Email.class);
    }
}
