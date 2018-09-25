/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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

import java.time.ZonedDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.annotations.VisibleForTesting;

public class CheckPast implements ConstraintValidator<Past, ZonedDateTime>
{
    private boolean mandatory;

    @Override
    public void initialize(Past past)
    {
        mandatory = past.mandatory();
    }

    @Override
    public boolean isValid(ZonedDateTime validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    private boolean isValidValue(ZonedDateTime validationObject)
    {
        return validationObject.isBefore(now());
    }

    @VisibleForTesting
    protected ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }
}