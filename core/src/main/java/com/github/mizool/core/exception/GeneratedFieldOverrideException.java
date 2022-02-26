package com.github.mizool.core.exception;

import lombok.Getter;
import lombok.NonNull;

/**
 * Thrown when an attempt is made to set a value for a field which is automatically generated or maintained, such as the
 * identifier or timestamps.
 */
public class GeneratedFieldOverrideException extends AbstractUnprocessableEntityException
{
    @Getter
    private final String fieldName;

    public GeneratedFieldOverrideException(@NonNull String fieldName)
    {
        super(String.format("Generated field %s must not be specified", fieldName));
        this.fieldName = fieldName;
    }
}
