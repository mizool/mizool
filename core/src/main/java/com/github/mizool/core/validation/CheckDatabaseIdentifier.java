package com.github.mizool.core.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckDatabaseIdentifier implements ConstraintValidator<DatabaseIdentifier, Object>
{
    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]\\w*");

    private boolean mandatory;

    @Override
    public void initialize(DatabaseIdentifier identifier)
    {
        mandatory = identifier.mandatory();
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
                .matches() && validationString.length() <= 48;
        }
        return valid;
    }
}
