/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestUuidValueAnnotation
{
    @RequiredArgsConstructor
    private static class TestData
    {
        @UuidValue(mandatory = false)
        private final String enumValue;
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

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValues(String value)
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestData(value));
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValues(String value)
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(value), UuidValue.class);
    }
}