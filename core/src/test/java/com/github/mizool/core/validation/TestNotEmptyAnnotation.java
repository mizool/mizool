package com.github.mizool.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.AllArgsConstructor;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class TestNotEmptyAnnotation
{

    @AllArgsConstructor
    private final class TestData
    {
        @NotEmpty(mandatory = true)
        private String content;
    }

    private Set<ConstraintViolation<TestData>> runValidator(TestData testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }

    @Test
    public void testValidationOfNullValue()
    {
        TestData testData = new TestData(null);
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(NotEmpty.class.getName());
    }

    @Test
    public void testValidationOfEmptyValue()
    {
        TestData testData = new TestData("");
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).hasSize(1);
        ConstraintViolation<TestData> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(NotEmpty.class.getName());
    }

    @Test
    public void testValidationOfAcceptableValue()
    {
        TestData testData = new TestData("content");
        Set<ConstraintViolation<TestData>> violations = runValidator(testData);
        assertThat(violations).isEmpty();
    }
}
