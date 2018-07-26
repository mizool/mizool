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

import java.util.List;

import lombok.AllArgsConstructor;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class TestIdentifierValueAnnotation
{
    private static final String EMPTY = "";
    private static final String IDENTIFIER = "foo";

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

    @Test
    public void testValidationOfUnacceptableValue()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestData(EMPTY), IdentifierValue.class);
    }

    @Test
    public void testValidationOfUnacceptableListValue()
    {
        ValidatorAnnotationTests.assertUnacceptableValue(new TestListData(Lists.newArrayList(IDENTIFIER, EMPTY)),
            IdentifierValue.class);
    }

    @Test
    public void testValidationOfAcceptableValue()
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestData(IDENTIFIER));
    }

    @Test
    public void testNonMandatory()
    {
        ValidatorAnnotationTests.assertAcceptableValue(new TestData(null));
    }
}