package com.github.mizool.core.validation;

import java.util.UUID;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckUuidValue implements ConstraintValidator<UuidValue, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(UuidValue uuidValue)
    {
        mandatory = uuidValue.mandatory();
    }

    @Override
    public final boolean isValid(
        Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        try
        {
            if (validationObject instanceof String)
            {
                String validationString = (String) validationObject;
                UUID.fromString(validationString);
                valid = true;
            }
        }
        catch (IllegalArgumentException ignored)
        {
        }
        return valid;
    }
}
