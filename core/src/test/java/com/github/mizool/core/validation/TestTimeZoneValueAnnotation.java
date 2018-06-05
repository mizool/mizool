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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class TestTimeZoneValueAnnotation
{
    @RequiredArgsConstructor
    private static class TestData
    {
        @TimeZoneValue(mandatory = false)
        private final String timeZoneValue;
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValues(String timeZone)
    {
        TestData testData = new TestData(timeZone);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{
            { null }, { "Europe/Paris" }, { "Europe/Berlin" }, { "Asia/Tokyo" }, { "UTC" }, { "GMT" }
        };
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValues(String timeZone)
    {
        TestData testData = new TestData(timeZone);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(TimeZoneValue.class.getName());
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{
            { "" }, { "UT" }, { "+2:00" }, { "GMT+2" }, { "UTC-01:00" }, { "Europe/Dortmund" }
        };
    }

    private Set<ConstraintViolation<TestData>> runValidator(TestData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }
}