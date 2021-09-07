package com.github.mizool.core.validation;

import java.time.Clock;
import java.time.ZonedDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckPast implements ConstraintValidator<Past, Object>
{
    private final Clock clock;

    private boolean mandatory;

    public CheckPast()
    {
        clock = Clock.systemDefaultZone();
    }

    @Override
    public void initialize(Past past)
    {
        mandatory = past.mandatory();
    }

    @Override
    public boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        if (validationObject instanceof ZonedDateTime)
        {
            ZonedDateTime validationZonedDateTime = (ZonedDateTime) validationObject;
            valid = validationZonedDateTime.isBefore(ZonedDateTime.now(clock));
        }
        return valid;
    }
}
