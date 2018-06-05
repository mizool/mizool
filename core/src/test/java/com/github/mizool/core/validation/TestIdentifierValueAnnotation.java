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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.AllArgsConstructor;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
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

    private Set<ConstraintViolation<TestIdentifierValueAnnotation.TestData>> runValidator(TestIdentifierValueAnnotation.TestData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }

    private Set<ConstraintViolation<TestIdentifierValueAnnotation.TestListData>> runValidator(
        TestIdentifierValueAnnotation.TestListData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }

    @Test
    public void testValidationOfUnacceptableValue()
    {
        TestIdentifierValueAnnotation.TestData testData = new TestIdentifierValueAnnotation.TestData(EMPTY);
        Set<ConstraintViolation<TestIdentifierValueAnnotation.TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestIdentifierValueAnnotation.TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(IdentifierValue.class.getName());
    }

    @Test
    public void testValidationOfUnacceptableListValue()
    {
        TestIdentifierValueAnnotation.TestListData
            testData
            = new TestIdentifierValueAnnotation.TestListData(Lists.newArrayList(IDENTIFIER, EMPTY));
        Set<ConstraintViolation<TestIdentifierValueAnnotation.TestListData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestIdentifierValueAnnotation.TestListData> violation = Iterables.getFirst(violations,
            null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(IdentifierValue.class.getName());
    }

    @Test
    public void testValidationOfAcceptableValue()
    {
        TestIdentifierValueAnnotation.TestData testData = new TestIdentifierValueAnnotation.TestData(IDENTIFIER);
        Set<ConstraintViolation<TestIdentifierValueAnnotation.TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testNonMandatory()
    {
        TestIdentifierValueAnnotation.TestData testData = new TestIdentifierValueAnnotation.TestData(null);
        Set<ConstraintViolation<TestIdentifierValueAnnotation.TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }
}