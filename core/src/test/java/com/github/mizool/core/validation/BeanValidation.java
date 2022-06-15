package com.github.mizool.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.experimental.UtilityClass;

import com.google.common.collect.Iterables;

@UtilityClass
class BeanValidation
{
    public <T> void assertAcceptableValue(T value)
    {
        Set<ConstraintViolation<T>> violations = runValidator(value);
        assertThat(violations).isEmpty();
    }

    public <T> void assertUnacceptableValue(T value, Class<? extends Annotation> annotationClass)
    {
        Set<ConstraintViolation<T>> violations = runValidator(value);
        assertThat(violations).hasSize(1);
        ConstraintViolation<T> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(annotationClass.getName());
    }

    private <T> Set<ConstraintViolation<T>> runValidator(T testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }
}
