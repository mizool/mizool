package com.github.mizool.core.rest.errorhandling;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.SetMultimap;

public class ConstraintViolationMapper
{
    public ErrorMessageDto fromPojo(Iterable<ConstraintViolation<?>> constraintViolations)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        for (ConstraintViolation<?> violation : constraintViolations)
        {
            recordConstraintViolation(violation, errors);
        }
        return ErrorMessageDto.builder().errors(errors.asMap()).build();
    }

    private void recordConstraintViolation(ConstraintViolation<?> violation, SetMultimap<String, ErrorDto> target)
    {
        String errorId = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();

        Path.Node lastProperty = Iterators.getLast(violation.getPropertyPath().iterator());
        String propertyName = lastProperty.getName();

        ErrorDto errorDto = new ErrorDto(errorId, null);
        target.put(propertyName, errorDto);
    }
}