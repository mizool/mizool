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

    /**
     * To avoid stack overflows in the regex engine, we have to limit the number of characters in the email address.<br>
     * The arbitrary number of 100 characters was chosen, as we fear that the 320 characters allowed by the
     * specification might already be too much.<br>
     * See <a href="https://rules.sonarsource.com/java/RSPEC-5998">Regular expressions should not overflow the stack</a>
     * for details.
     */
    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        if (validationObject instanceof String)
        {
            String validationString = (String) validationObject;
            valid = validationString.length() <= 100 &&
                PATTERN.matcher(validationString)
                    .matches();
        }
        return valid;
    }
}
