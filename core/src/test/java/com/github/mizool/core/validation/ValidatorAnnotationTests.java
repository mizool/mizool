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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import lombok.experimental.UtilityClass;

import com.google.common.collect.Iterables;

@UtilityClass
class ValidatorAnnotationTests
{
    public static <T> void assertAcceptableValue(T value)
    {
        Set<ConstraintViolation<T>> violations = runValidator(value);
        assertThat(violations).isEmpty();
    }

    public static <T> void assertUnacceptableValue(T value, Class<? extends Annotation> annotationClass)
    {
        Set<ConstraintViolation<T>> violations = runValidator(value);
        assertThat(violations).hasSize(1);
        ConstraintViolation<T> violation = Iterables.getFirst(violations, null);
        String violatedAnnotation = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();
        assertThat(violatedAnnotation).isEqualTo(annotationClass.getName());
    }

    private static <T> Set<ConstraintViolation<T>> runValidator(T testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testData);
    }
}