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

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.collect.Lists;

public class TestConstraintValidators
{
    @RequiredArgsConstructor
    private enum Type
    {
        OPTIONAL(false),
        MANDATORY(true);

        @Getter
        private final boolean isMandatory;
    }

    @RequiredArgsConstructor
    private enum Validation
    {
        SUCCESS(true),
        FAILURE(false);

        @Getter
        private final boolean passed;
    }

    private static final Predicate<Object> PREDICATE_NOT_CALLED = o -> {
        throw new CodeInconsistencyException("this should never be called");
    };

    private static final List<Object> LIST_WITH_NULL_ELEMENT = Collections.singletonList(null);

    @Test(dataProvider = "data")
    public <T> void test(
        String name,
        Type type,
        T validationObject,
        Predicate<T> scalarCheck,
        Class<T> scalarClass,
        Validation validationResult)
    {
        boolean result = ConstraintValidators.isValid(validationObject, type.isMandatory(), scalarCheck);
        assertThat(result).isEqualTo(validationResult.isPassed());
    }

    @DataProvider
    public Object[][] data()
    {
        return new Object[][]{
            // a null object is unacceptable for mandatory fields
            {
                "null as value of a mandatory field",
                Type.MANDATORY,
                null,
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.FAILURE
            },

            // a null object is perfectly fine for optional fields
            {
                "null as value of an optional field",
                Type.OPTIONAL,
                null,
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.SUCCESS
            },

            // we decided that mandatory = true" accepting an empty list would not be intuitive
            {
                "empty list in mandatory field",
                Type.MANDATORY,
                Collections.emptyList(),
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.FAILURE
            },

            // this is okay, if we want to have a non-empty list we would use a @Size constraint
            {
                "empty list in optional field",
                Type.OPTIONAL,
                Collections.emptyList(),
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.SUCCESS
            },

            {
                "valid value in mandatory field",
                Type.MANDATORY,
                "foo",
                (Predicate<String>) s -> true,
                String.class,
                Validation.SUCCESS
            }, {
                "valid value in optional field",
                Type.OPTIONAL,
                "foo",
                (Predicate<String>) s -> true,
                String.class,
                Validation.SUCCESS
            }, {
                "invalid value in mandatory field",
                Type.MANDATORY,
                "foo",
                (Predicate<String>) s -> false,
                String.class,
                Validation.FAILURE
            }, {
                "invalid value in optional field",
                Type.OPTIONAL,
                "foo",
                (Predicate<String>) s -> false,
                String.class,
                Validation.FAILURE
            },

            {
                "list with valid value in mandatory field",
                Type.MANDATORY,
                Collections.singletonList("foo"),
                (Predicate<String>) s -> true,
                String.class,
                Validation.SUCCESS
            }, {
                "list with valid value in optional field",
                Type.OPTIONAL,
                Collections.singletonList("foo"),
                (Predicate<String>) s -> true,
                String.class,
                Validation.SUCCESS
            }, {
                "list with invalid value in mandatory field",
                Type.MANDATORY,
                Collections.singletonList("foo"),
                (Predicate<String>) s -> false,
                String.class,
                Validation.FAILURE
            }, {
                "list with invalid value in optional field",
                Type.OPTIONAL,
                Collections.singletonList("foo"),
                (Predicate<String>) s -> false,
                String.class,
                Validation.FAILURE
            },

            {
                "list with null element in optional field",
                Type.OPTIONAL,
                LIST_WITH_NULL_ELEMENT,
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.FAILURE
            }, {
                "list with null element in mandatory field",
                Type.MANDATORY,
                LIST_WITH_NULL_ELEMENT,
                PREDICATE_NOT_CALLED,
                String.class,
                Validation.FAILURE
            }, {
                "list including a null element in mandatory field",
                Type.MANDATORY,
                Lists.newArrayList("foo", null, "bar"),
                (Predicate<String>) s -> true,
                String.class,
                Validation.FAILURE
            }, {
                "list including a null element in mandatory field",
                Type.MANDATORY,
                Lists.newArrayList("foo", "bar", null),
                (Predicate<String>) s -> true,
                String.class,
                Validation.FAILURE
            }
        };
    }

    /*
    additional tests:
     null values in a collection result in false withozt the predicate having been called with the null value
        remark: for the non-null values (at least those before the null), the predicate can must have been called
    notes:
     - jens does not care whether the predicate throws exceptions or not. sven says it shouldn't.
     */
}