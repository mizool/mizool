/**
 * Copyright 2018-2019 incub8 Software Labs GmbH
 * Copyright 2018-2019 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            { "b@b.de" }, { "bob@example.com" }, { null }
        };
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "b" }, { "b@" }, { "b@b" }, { "b@b." }, { ".b@b." }
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