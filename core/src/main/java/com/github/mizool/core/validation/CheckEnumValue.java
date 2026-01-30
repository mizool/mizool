package com.github.mizool.core.validation;

import com.google.common.base.Enums;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckEnumValue implements ConstraintValidator<EnumValue, Object>
{
    @SuppressWarnings("rawtypes")
    private Class<? extends Enum> enumeration;
    private boolean mandatory;

    @Override
    public void initialize(EnumValue enumValue)
    {
        this.enumeration = enumValue.value();
        mandatory = enumValue.mandatory();
    }

    @Override
    public final boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return ConstraintValidators.isValid(validationObject, mandatory, this::isValidValue);
    }

    @SuppressWarnings("unchecked")
    private boolean isValidValue(Object validationObject)
    {
        boolean valid = false;
        if (validationObject instanceof String)
        {
            valid = Enums.getIfPresent(enumeration, (String) validationObject).isPresent();
        }
        return valid;
    }
}
