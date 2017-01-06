package com.github.mizool.validation;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

abstract class AbstractTypeMappingValidator<A extends Annotation> implements ConstraintValidator<A, String>
{
    @Override
    public void initialize(A annotation)
    {
    }

    @Override
    public final boolean isValid(String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return validationObject == null ||
            !validationObject.isEmpty() && isNonNullNonEmptyStringValid(validationObject, constraintValidatorContext);
    }

    protected abstract boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext);
}