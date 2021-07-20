package com.github.mizool.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckDatabaseIdentifier implements ConstraintValidator<DatabaseIdentifier, Object>
{
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
            return !validationString.isEmpty() &&
                validationString.matches("[a-zA-Z][a-zA-Z0-9_]*") &&
                validationString.length() <= 48;
        }
        return valid;
    }
}
