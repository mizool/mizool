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

import lombok.AllArgsConstructor;

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
}