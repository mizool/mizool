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

public class TestUuidValueAnnotation
{
    @RequiredArgsConstructor
    private static class TestData
    {
        @UuidValue(mandatory = false)
        private final String enumValue;
    }
    
    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValues(String enumValue)
    {
        TestData testData = new TestData(enumValue);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }

    @DataProvider
    private Object[][] acceptableValues()
    {
        return new Object[][]{ { null }, { "ddae0767-6dbc-4d6f-8fbe-2ba9ffd55e4a" } };
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValues(String enumValue)
    {
        TestData testData = new TestData(enumValue);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(UuidValue.class.getName());
    }

    @DataProvider
    private Object[][] unacceptableValues()
    {
        return new Object[][]{ { "" }, { "MOEP" } };
    }

    private Set<ConstraintViolation<TestData>> runValidator(TestData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }
}