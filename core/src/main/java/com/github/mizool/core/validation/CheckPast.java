/**
 * Copyright 2018-2019 incub8 Software Labs GmbH
 * Copyright 2018-2019 protel Hotelsoftware GmbH
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

import java.time.Clock;
import java.time.ZonedDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.google.common.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckPast implements ConstraintValidator<Past, Object>
{
    private final Clock clock;

    private boolean mandatory;

    public CheckPast()
    {
        clock = Clock.systemDefaultZone();
    }

    @Override
    public void initialize(Past past)
    {
        mandatory = past.mandatory();
    }

    @Override
    public boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue, ZonedDateTime.class);
    }

    private boolean isValidValue(ZonedDateTime validationObject)
    {
        return validationObject.isBefore(ZonedDateTime.now(clock));
    }
}