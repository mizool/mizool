package com.github.mizool.core.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEmail implements ConstraintValidator<Email, Object>
{
    private static final Pattern PATTERN = Pattern.compile("[^@\\s]+@([^\\s.@]+\\.)+[^\\s.@]+");

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
            valid = PATTERN.matcher(validationString)
                .matches();
        }
        return valid;
    }
}
