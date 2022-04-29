package com.github.mizool.core.validation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class TestLanguageTagValueAnnotation
{
    @AllArgsConstructor
    private final class TestData
    {
        @LanguageTagValue(mandatory = false)
        private String languageTag;
    }

    @RequiredArgsConstructor
    private static class TestListData
    {
        @LanguageTagValue(mandatory = false)
        private final List<String> languageTags;
    }

    @RequiredArgsConstructor
    private static class WrongDataTypeList
    {
        @LanguageTagValue(mandatory = false)
        private final List<Integer> languageTags;
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{
            { null }, { "DE-de" }, { "EN-gb" }, { "DE-gb" }, { "FR-fr" }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "foo" }, { "german" }, { "DE" }, { "de" }, { "DE-german" }
        };
    }

    @DataProvider
    private Object[][] acceptableListValues()
    {
        return new Object[][]{
            { null }, { ImmutableList.of("DE-de") }, { ImmutableList.of("DE-de", "DE-gb", "FR-fr") }
        };
    }

    @DataProvider
    private Object[][] unacceptableListValues()
    {
        return new Object[][]{
            { ImmutableList.of("") }, { ImmutableList.of("foo") }, { ImmutableList.of("DE-de", "german") }
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
        BeanValidation.assertUnacceptableValue(new TestData(value), LanguageTagValue.class);
    }

    @Test(dataProvider = "acceptableListValues")
    public void testValidationOfAcceptableValues(List<String> values)
    {
        BeanValidation.assertAcceptableValue(new TestListData(values));
    }

    @Test(dataProvider = "unacceptableListValues")
    public void testValidationOfUnacceptableListValues(List<String> values)
    {
        BeanValidation.assertUnacceptableValue(new TestListData(values), LanguageTagValue.class);
    }

    @Test
    public void testHandlesWrongDataType()
    {
        BeanValidation.assertUnacceptableValue(new WrongDataTypeList(ImmutableList.of(1, 5)),
            LanguageTagValue.class);
    }
}
