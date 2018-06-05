package com.github.mizool.core.validation;

import java.util.function.Predicate;

public class ConstraintValidators
{
    public static <T> boolean isValid(T validationObject, boolean mandatory, Predicate<T> isValidValue)
    {
        return isNullButOptional(validationObject, mandatory) || isNotNullAndValid(validationObject, isValidValue);
    }

    private static <T> boolean isNullButOptional(T validationObject, boolean mandatory)
    {
        return validationObject == null && !mandatory;
    }

    private static <T> boolean isNotNullAndValid(T validationObject, Predicate<T> isValidValue)
    {
        return validationObject != null && isValidValue.test(validationObject);
    }
}