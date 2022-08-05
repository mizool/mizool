package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestDatabaseIdentifierAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @DatabaseIdentifier(mandatory = false)
        private String identifier;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @DatabaseIdentifier(mandatory = false)
        private final List<String> identifiers;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @DatabaseIdentifier(mandatory = false)
        private final List<Integer> identifiers;
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{
            { null },
            { "login" },
            { "login_" },
            { "login23" },
            { "LogIN" },
            { "LoremIpsumDolorSitAmetConseteturSadipscingElitrS" }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "" },
            { "_login" },
            { "log(in" },
            { "login!" },
            { "@login" },
            { "log\"in" },
            { "\"login\"" },
            { "\"login\"()" },
            { "log me in" },
            { "LoremIpsumDolorSitAmetConseteturSadipscingElitrSe" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { null }, { ImmutableList.of("login") }, {
            ImmutableList.of("login_", "LogIN", "LoremIpsumDolorSitAmetConseteturSadipscingElitrS")
        }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("_login") }, { ImmutableList.of("login", "log(in") }
        };
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValue(String value)
    {
        BeanValidation.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValue(String value)
    {
        BeanValidation.assertUnacceptableValue(new TestData(value), DatabaseIdentifier.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), DatabaseIdentifier.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            DatabaseIdentifier.class);
    }
}
