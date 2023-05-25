package com.github.mizool.core.validation;

import java.util.TimeZone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckTimeZoneValue implements ConstraintValidator<TimeZoneValue, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(TimeZoneValue timeZoneValue)
    {
        mandatory = timeZoneValue.mandatory();
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
            if (!validationString.isEmpty())
            {
                TimeZone timeZone = TimeZone.getTimeZone(validationString);
                valid = timeZone.getID().equals(validationString);
            }
        }
        return valid;
    }
}
