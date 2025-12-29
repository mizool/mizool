package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
        return new Object[][]{ { null }, { List.of("b@b.de") }, { List.of("b@b.de", "bob@example.com") } };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{ { List.of("") }, { List.of("b") }, { List.of("bob@example.com", "b@") } };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValue(String value)
    {
        BeanValidation.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValue(String value)
    {
        BeanValidation.assertUnacceptableValue(new TestData(value), Email.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), Email.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(List.of(1, 5)), Email.class);
    }
}
