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
package com.github.mizool.core.rest.errorhandling;

import java.util.Collection;
import java.util.Map;

import lombok.Builder;
import lombok.Value;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Value
@Builder(toBuilder = true)
public class ErrorMessageDto
{
    private final Map<String, Collection<ErrorDto>> errors;

    public ErrorMessageDto combineWith(ErrorMessageDto other)
    {
        SetMultimap<String, ErrorDto> combined = HashMultimap.create();

        errors.forEach(combined::putAll);
        other.getErrors().forEach(combined::putAll);

        return toBuilder().errors(combined.asMap()).build();
    }
}