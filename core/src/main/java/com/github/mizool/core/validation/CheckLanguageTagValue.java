/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.mizool.core.validation;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

import com.google.common.collect.ImmutableSet;

public class CheckLanguageTagValue extends AbstractTypeMappingValidator<LanguageTagValue>
{
    private static final Set<String> LANGUAGE_CODES = ImmutableSet.copyOf(Locale.getISOLanguages());
    private static final Set<String> COUNTRY_CODES = ImmutableSet.copyOf(Locale.getISOCountries());

    @Override
    public void initialize(LanguageTagValue notEmpty)
    {
    }

    @Override
    protected boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        Locale locale = Locale.forLanguageTag(validationObject);
        return isValidLanguage(locale) && isValidCountry(locale);
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