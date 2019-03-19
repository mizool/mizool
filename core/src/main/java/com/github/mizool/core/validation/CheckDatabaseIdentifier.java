/**
 * Copyright 2017-2019 incub8 Software Labs GmbH
 * Copyright 2017-2019 protel Hotelsoftware GmbH
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