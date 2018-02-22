/**
 * Copyright 2017 incub8 Software Labs GmbH
 * Copyright 2017 protel Hotelsoftware GmbH
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckEnumValue implements ConstraintValidator<EnumValue, String>
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
    public final boolean isValid(String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean valid = false;
        if (validationObject != null)
        {
            try
            {
                Enum.valueOf(enumeration, validationObject);
                valid = true;
            }
            catch (Exception ignored)
            {
            }
        }
        else if (!mandatory)
        {
            valid = true;
        }
        return valid;
    }
}