/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEnumValue implements ConstraintValidator<EnumValue, Object>
{
    private Class enumeration;
    private boolean mandatory;

    @Override
    public void initialize(EnumValue enumValue)
    {
        this.enumeration = enumValue.value();
        mandatory = enumValue.mandatory();
    }

    @Override
    public final boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(Object validationObject)
    {
        if (validationObject instanceof String)
        {
            return isValidValue((String) validationObject);
        }
        else if (validationObject instanceof List)
        {
            return isValidValue((List) validationObject);
        }
        return false;
    }

    private boolean isValidValue(String validationObject)
    {
        boolean valid;
        try
        {
            Enum.valueOf(enumeration, validationObject);
            valid = true;
        }
        catch (@SuppressWarnings("squid:S1166") IllegalArgumentException | NullPointerException ignored)
        {
            valid = false;
        }
        return valid;
    }

    private boolean isValidValue(List validationObject)
    {
        boolean valid = true;
        for (Object value : validationObject)
        {
            try
            {
                Enum.valueOf(enumeration, (String) value);
                valid = true;
            }
            catch (@SuppressWarnings("squid:S1166") IllegalArgumentException | NullPointerException ignored)
            {
                valid = false;
                break;
            }
        }
        return valid;
    }
}