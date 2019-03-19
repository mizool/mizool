/**
 * Copyright 2019 incub8 Software Labs GmbH
 * Copyright 2019 protel Hotelsoftware GmbH
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

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.Streams;

@UtilityClass
public class ConstraintValidators
{
    public static <T> boolean isValid(T validationObject, boolean mandatory, Predicate<T> isValidValue)
    {
        boolean result = isNullButOptional(validationObject, mandatory);
        if (validationObject instanceof Iterable)
        {
            Iterable<T> validationObjects = (Iterable<T>) validationObject;
            result = result || isNotNullAndValid(validationObjects, isValidValue);
        }
        else
        {
            result = result || isNotNullAndValid(validationObject, isValidValue);
        }

        return result;
    }

    private static <T> boolean isNullButOptional(T validationObject, boolean mandatory)
    {
        return validationObject == null && !mandatory;
    }

    private static <T> boolean isNotNullAndValid(T validationObject, Predicate<T> isValidValue)
    {
        return validationObject != null && isValidValue.test(validationObject);
    }

    private static <T> boolean isNotNullAndValid(Iterable<T> validationObjects, Predicate<T> isValidValue)
    {
        return Streams.sequential(validationObjects).allMatch(isValidValue);
    }
}