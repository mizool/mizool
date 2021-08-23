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
        ErrorMessageDto errorMessage = ErrorMessageDto.builder()
            .errors(errors.asMap())
            .build();
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
    }

    public void recordRuleViolation(RuleViolation violation, SetMultimap<String, ErrorDto> target)
    {
        ErrorDto errorDto = new ErrorDto(violation.getErrorId(), null);
        target.put(violation.getFieldName(), errorDto);
    }
}