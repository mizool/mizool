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
package com.github.mizool.core.rest.errorhandling;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.RuleViolation;
import com.github.mizool.core.exception.RuleViolationException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Slf4j
public class RuleViolationMapper
{
    public ErrorResponse handleRuleViolationError(RuleViolationException e)
    {
        log.debug("Rule violation error", e);
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();

        for (RuleViolation violation : e.getRuleViolations())
        {
            recordRuleViolation(violation, errors);
        }
        ErrorMessageDto errorMessage = ErrorMessageDto.builder().errors(errors.asMap()).build();
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
    }

    public void recordRuleViolation(RuleViolation violation, SetMultimap<String, ErrorDto> target)
    {
        ErrorDto errorDto = new ErrorDto(violation.getErrorId(), null);
        target.put(violation.getFieldName(), errorDto);
    }
}
