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

import java.util.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckUuidValue implements ConstraintValidator<UuidValue, String>
{
    private boolean mandatory;

    @Override
    public void initialize(UuidValue uuidValue)
    {
        mandatory = uuidValue.mandatory();
    }

    @Override
    public final boolean isValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(String validationObject)
    {
        boolean valid = false;
        try
        {
            UUID.fromString(validationObject);
            valid = true;
        }
        catch (IllegalArgumentException ignored)
        {
        }
        return valid;
    }
}