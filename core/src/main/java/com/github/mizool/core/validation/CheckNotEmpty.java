package com.github.mizool.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckNotEmpty implements ConstraintValidator<NotEmpty, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(NotEmpty notEmpty)
    {
        mandatory = notEmpty.mandatory();
    }

    @Override
    public boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        if (validationObject instanceof String)
        {
            String validationString = (String) validationObject;
            valid = !validationString.isEmpty();
        }
        return valid;
    }
}
