package com.github.mizool.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckNotEmpty implements ConstraintValidator<NotEmpty, String>
{
    @Override
    public void initialize(NotEmpty notEmpty)
    {
    }

    @Override
    public boolean isValid(String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return validationObject != null && !validationObject.isEmpty();
    }
}