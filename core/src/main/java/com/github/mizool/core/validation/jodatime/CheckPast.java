package com.github.mizool.core.validation.jodatime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.DateTime;

import com.google.common.annotations.VisibleForTesting;

public class CheckPast implements ConstraintValidator<Past, DateTime>
{
    private boolean mandatory;

    @Override
    public void initialize(Past past)
    {
        mandatory = past.mandatory();
    }

    @Override
    public boolean isValid(DateTime validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return isNullButOptional(validationObject) || (validationObject != null && isValidValue(validationObject));
    }

    private boolean isNullButOptional(DateTime validationObject)
    {
        return validationObject == null && !mandatory;
    }

    private boolean isValidValue(DateTime validationObject)
    {
        return validationObject.isBefore(now());
    }

    @VisibleForTesting
    protected DateTime now()
    {
        return DateTime.now();
    }
}