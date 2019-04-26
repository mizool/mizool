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

    private static final Predicate<Object> PREDICATE_NOT_CALLED = o -> {
        throw new CodeInconsistencyException("this should never be called");
    };

    private static final List<Object> LIST_WITH_NULL_ELEMENT = Collections.singletonList(null);

    @Test(dataProvider = "data")
    public void test(
        String name, Type type, Object validationObject, Predicate<Object> scalarCheck, boolean expectedValid)
    {
        boolean result = ConstraintValidators.isValid(validationObject, type.isMandatory(), scalarCheck);
        assertThat(result).isEqualTo(expectedValid);
    }

    @DataProvider
    public Object[][] data()
    {
        return new Object[][]{
            // a null object is unacceptable for mandatory fields
            { "null as value of a mandatory field", Type.MANDATORY, null, PREDICATE_NOT_CALLED, false },

            // a null object is perfectly fine for optional fields
            { "null as value of an optional field", Type.OPTIONAL, null, PREDICATE_NOT_CALLED, true },

            // we decided that mandatory = true" accepting an empty list would not be intuitive
            { "empty list in mandatory field", Type.MANDATORY, Collections.emptyList(), PREDICATE_NOT_CALLED, false },

            // this is okay, if we want to have a non-empty list we would use a @Size constraint
            { "empty list in optional field", Type.OPTIONAL, Collections.emptyList(), PREDICATE_NOT_CALLED, true },


            {
                "list with null element in optional field",
                Type.OPTIONAL,
                LIST_WITH_NULL_ELEMENT,
                PREDICATE_NOT_CALLED,
                false
            },
            {
                "list with null element in mandatory field",
                Type.MANDATORY,
                LIST_WITH_NULL_ELEMENT,
                PREDICATE_NOT_CALLED,
                false
            },
            };
    }

    /*

    additional tests:

     true predicate result is returned as true
     false predicate result is returned as false
     permutations to find loop errors: valid-invalid-valid results in false
     happy cases: valid value in a list results in true, both for optional and mandatory fields
     null values in a collection result in false withozt the predicate having been called with the null value
        remark: for the non-null values (at least those before the null), the predicate can must have been called
     when the predicate wants a string, it should not be called at all for a list of integers
     reject field values that are neither T nor Iterable<T>, e.g. an Animal

    other requirements:
        - when a field List<Integer> is annotated with a beanval check that calls ConstraintValidators with
          a predicate for T=String, this is a CodeInconsistencyException.



    notes:
     - to satisfy jens' curiosity, test this: when a DTO field wants a list of strings, but the client-supplied JSON
       has a list of numbers, we should never even reach a bean validation check.
     - encapsulate the expected true/false validation result in an enum to make the data provider more readable, e.g. "Validation.PASSED/FAILED"
     - jens does not care whether the predicate throws exceptions or not. sven says it shouldn't.

     */
}