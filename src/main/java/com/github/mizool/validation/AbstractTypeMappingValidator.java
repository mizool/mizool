/**
 *  Copyright 2017 incub8 Software Labs GmbH
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
package com.github.mizool.validation;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

abstract class AbstractTypeMappingValidator<A extends Annotation> implements ConstraintValidator<A, String>
{
    @Override
    public void initialize(A annotation)
    {
    }

    @Override
    public final boolean isValid(String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return validationObject == null ||
            !validationObject.isEmpty() && isNonNullNonEmptyStringValid(validationObject, constraintValidatorContext);
    }

    protected abstract boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext);
}