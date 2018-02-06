package com.github.mizool.core.rest.errorhandling;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;

public class ConstraintViolationMapper
{
    public ErrorMessageDto fromPojo(Iterable<ConstraintViolation<?>> constraintViolations)
    {
        ListMultimap<String, ErrorDto> errors = ArrayListMultimap.create();
        for (ConstraintViolation<?> violation : constraintViolations)
        {
            recordConstraintViolation(violation, errors);
        }
        return new ErrorMessageDto(errors.asMap());
    }

    private void recordConstraintViolation(ConstraintViolation<?> violation, ListMultimap<String, ErrorDto> target)
    {
        String errorId = violation.getConstraintDescriptor().getAnnotation().annotationType().getName();

        Path.Node lastProperty = Iterators.getLast(violation.getPropertyPath().iterator());
        String propertyName = lastProperty.getName();

        ErrorDto errorDto = new ErrorDto(errorId, null);
        target.put(propertyName, errorDto);
    }
}