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