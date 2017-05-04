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

import javax.validation.ConstraintValidatorContext;

public class CheckEnumValue extends AbstractTypeMappingValidator<EnumValue>
{
    private Class enumeration;

    @Override
    public void initialize(EnumValue annotation)
    {
        this.enumeration = annotation.value();
    }

    @Override
    protected final boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean valid = true;
        try
        {
            Enum.valueOf(enumeration, validationObject);
        }
        catch (Exception e)
        {
            valid = false;
        }
        return valid;
    }
}