package com.github.mizool.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEmail implements ConstraintValidator<Email, String>
{
    private boolean mandatory;

    @Override
    public void initialize(Email email)
    {
        mandatory = email.mandatory();
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
        return validationObject.matches(".+[@].+\\..+");
    }
}