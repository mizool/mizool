package com.github.mizool.validation;

import javax.validation.ConstraintValidatorContext;

public class CheckEnumValue extends AbstractTypeMappingValidator<EnumValue>
{
    private Class enumeration;

    @Override
    public void initialize(EnumValue annotation)
    {
        this.enumeration = annotation.value();
    }

    @Override
    protected final boolean isNonNullNonEmptyStringValid(
        String validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        boolean valid = true;
        try
        {
            Enum.valueOf(enumeration, validationObject);
        }
        catch (Exception e)
        {
            valid = false;
        }
        return valid;
    }
}