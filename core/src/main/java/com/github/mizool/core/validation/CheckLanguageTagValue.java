package com.github.mizool.core.validation;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.ImmutableSet;

public class CheckLanguageTagValue implements ConstraintValidator<LanguageTagValue, Object>
{
    private static final Set<String> LANGUAGE_CODES = ImmutableSet.copyOf(Locale.getISOLanguages());
    private static final Set<String> COUNTRY_CODES = ImmutableSet.copyOf(Locale.getISOCountries());
    private boolean mandatory;

    @Override
    public void initialize(LanguageTagValue languageTagValue)
    {
        mandatory = languageTagValue.mandatory();
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
            Locale locale = Locale.forLanguageTag(validationString);
            valid = !validationString.isEmpty() && isValidLanguage(locale) && isValidCountry(locale);
        }
        return valid;
    }

    private boolean isValidLanguage(Locale locale)
    {
        String languageCode = locale.getLanguage();
        return LANGUAGE_CODES.contains(languageCode);
    }

    private boolean isValidCountry(Locale locale)
    {
        String countryCode = locale.getCountry();
        return COUNTRY_CODES.contains(countryCode);
    }
}
