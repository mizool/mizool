package com.github.mizool.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.AllArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class TestEmailAnnotation
{

    @AllArgsConstructor
    private final class TestData
    {
        @Email(mandatory = false)
        private String email;
    }

    private Set<ConstraintViolation<TestData>> runValidator(TestData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }

    @DataProvider(name = "unacceptableValues")
    public Object[][] createUnacceptableValues()
    {
        return new Object[][]{
            { "b" }, { "b@" }, { "b@b" }, { "b@b." }, { ".b@b." }
        };
    }

    @DataProvider(name = "acceptableValues")
    public Object[][] createAcceptableValues()
    {
        return new Object[][]{
            { "b@b.de" }, { "bob@example.com" }, { null }
        };
    }

    @Test(dataProvider = "unacceptableValues")
    public void testValidationOfUnacceptableValue(String value)
    {
        TestData testData = new TestData(value);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(Email.class.getName());
    }

    @Test(dataProvider = "acceptableValues")
    public void testValidationOfAcceptableValue(String value)
    {
        TestData testData = new TestData(value);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }
}