package com.github.mizool.core.validation;

import java.util.TimeZone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckTimeZoneValue implements ConstraintValidator<TimeZoneValue, String>
{
    private boolean mandatory;

    @Override
    public void initialize(TimeZoneValue timeZoneValue)
    {
        mandatory = timeZoneValue.mandatory();
    }

    @Override
    public boolean isValid(String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return isNullButOptional(validationObject) || (validationObject != null && isValidValue(validationObject));
    }

    private boolean isNullButOptional(String validationObject)
    {
        return validationObject == null && !mandatory;
    }

    private boolean isValidValue(String validationObject)
    {
        boolean valid = false;
        if (!validationObject.isEmpty())
        {
            TimeZone timeZone = TimeZone.getTimeZone(validationObject);
            valid = timeZone.getID().equals(validationObject);
        }
        return valid;
    }
}