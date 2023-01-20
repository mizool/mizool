package com.github.mizool.core.validation;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.ImmutableSet;

public class CheckCountryCode implements ConstraintValidator<CountryCode, Object>
{
    private static final Set<String> COUNTRY_CODES = ImmutableSet.copyOf(Locale.getISOCountries());

    private boolean mandatory;

    @Override
    public void initialize(CountryCode countryCode)
    {
        mandatory = countryCode.mandatory();
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
            valid = !validationString.isEmpty() && COUNTRY_CODES.contains(validationString);
        }
        return valid;
    }
}
