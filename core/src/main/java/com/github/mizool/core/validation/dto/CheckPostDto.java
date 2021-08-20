package com.github.mizool.core.validation.dto;

import javax.validation.ConstraintValidatorContext;

import com.github.mizool.core.validation.ConstraintValidatorAdapter;
import com.github.mizool.core.validation.ConstraintValidators;

public class CheckPostDto extends ConstraintValidatorAdapter<PostDto, Dto>
{
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
