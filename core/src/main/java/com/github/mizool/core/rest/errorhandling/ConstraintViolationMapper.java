package com.github.mizool.core.rest.errorhandling;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.SetMultimap;

@Slf4j
@RequiredArgsConstructor
public class ConstraintViolationMapper
{
    private final GlobalParametersSupplier globalParametersSupplier;

    public ErrorResponse handleConstraintViolationError(ConstraintViolationException e)
    {
        log.debug("Validation error", e);
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();

        for (ConstraintViolation<?> violation : e.getConstraintViolations())
        {
            recordConstraintViolation(violation, errors);
        }
        ErrorMessageDto errorMessage = ErrorMessageDto.builder()
            .errors(errors.asMap())
            .globalParameters(globalParametersSupplier.get())
            .build();
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
    }

    private void recordConstraintViolation(ConstraintViolation<?> violation, SetMultimap<String, ErrorDto> target)
    {
        String errorId = violation.getConstraintDescriptor()
            .getAnnotation()
            .annotationType()
            .getName();

        Path.Node lastProperty = Iterators.getLast(violation.getPropertyPath()
            .iterator());
        String propertyName = lastProperty.getName();

        ErrorDto errorDto = new ErrorDto(errorId, null);
        target.put(propertyName, errorDto);
    }
}