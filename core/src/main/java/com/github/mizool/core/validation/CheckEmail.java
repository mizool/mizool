package com.github.mizool.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEmail implements ConstraintValidator<Email, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(Email email)
    {
        mandatory = email.mandatory();
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
            valid = validationString.matches(".+[@].+\\..+");
        }
        return valid;
    }
}
