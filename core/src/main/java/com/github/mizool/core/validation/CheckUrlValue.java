package com.github.mizool.core.validation;

import java.net.MalformedURLException;
import java.net.URL;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckUrlValue implements ConstraintValidator<UrlValue, Object>
{
    private boolean mandatory;

    @Override
    public void initialize(UrlValue urlValue)
    {
        mandatory = urlValue.mandatory();
    }

    @Override
    public final boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        try
        {
            if (validationObject instanceof String)
            {
                String validationString = (String) validationObject;
                new URL(validationString);
                valid = true;
            }
        }
        catch (MalformedURLException e)
        {
            log.debug("{} is not a valid URL", validationObject, e);
        }
        return valid;
    }
}
