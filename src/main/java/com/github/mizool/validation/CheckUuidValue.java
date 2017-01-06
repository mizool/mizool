package com.github.mizool.validation;

import java.util.UUID;

import javax.validation.ConstraintValidatorContext;

public class CheckUuidValue extends AbstractTypeMappingValidator<UuidValue>
{
    @Override
    protected final boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean valid = true;
        try
        {
            UUID.fromString(validationObject);
        }
        catch (IllegalArgumentException e)
        {
            valid = false;
        }
        return valid;
    }
}