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
package com.github.mizool.core.validation.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.github.mizool.core.validation.ConstraintValidators;

public class CheckPostDto implements ConstraintValidator<PostDto, Dto>
{
    @Override
    public void initialize(PostDto constraintAnnotation)
    {
    }

    @Override
    public final boolean isValid(Dto dto, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(dto, true, this::isValidValue);
    }

    private boolean isValidValue(Dto dto)
    {
        return dto.getId() == null &&
            dto.getTimestamp() == null &&
            dto.getCreationTimestamp() == null &&
            dto.getModificationTimestamp() == null;
    }
}