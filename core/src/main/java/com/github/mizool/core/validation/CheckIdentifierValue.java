package com.github.mizool.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckIdentifierValue implements ConstraintValidator<IdentifierValue, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(IdentifierValue identifierValue)
    {
        mandatory = identifierValue.mandatory();
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
