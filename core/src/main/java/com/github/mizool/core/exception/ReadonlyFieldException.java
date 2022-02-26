package com.github.mizool.core.exception;

import lombok.Getter;
import lombok.NonNull;

/**
 * Thrown when an attempt is made to change the value of a field that is considered read-only for the current operation.
 * For example, it may be forbidden to change the name of a user group, although it must be set when creating one.
 */
@Getter
public class ReadonlyFieldException extends AbstractUnprocessableEntityException
{
    private final String fieldName;
    private final String recordId;

    public ReadonlyFieldException(@NonNull String fieldName)
    {
        super(String.format("Attempt to update readonly field %s", fieldName));
        this.fieldName = fieldName;
        recordId = null;
    }

    public ReadonlyFieldException(@NonNull String fieldName, @NonNull String recordId)
    {
        super(String.format("Attempt to update readonly field %s of '%s'", fieldName, recordId));
        this.fieldName = fieldName;
        this.recordId = recordId;
    }
}
