package com.github.mizool.core.validation.dto;

import com.github.mizool.core.validation.ConstraintValidatorAdapter;
import com.github.mizool.core.validation.ConstraintValidators;
import jakarta.validation.ConstraintValidatorContext;

public class CheckPutDto extends ConstraintValidatorAdapter<PutDto, Dto>
{
    @Override
    public final boolean isValid(Dto dto, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(dto, true, this::isValidValue);
    }

    private boolean isValidValue(Dto dto)
    {
        return dto.getId() != null && (
            dto.getTimestamp() != null ^
                (dto.getCreationTimestamp() != null && dto.getModificationTimestamp() != null));
    }
}
