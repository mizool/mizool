package com.github.mizool.core.validation;

import java.util.function.Predicate;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.Streams;

@UtilityClass
public class ConstraintValidators
{
    public static <T> boolean isValid(T validationObject, boolean mandatory, Predicate<T> isValidValue)
    {
        boolean result = isNullButOptional(validationObject, mandatory);
        if (validationObject instanceof Iterable)
        {
            Iterable<T> validationObjects = (Iterable<T>) validationObject;
            result = result || isNotNullAndValid(validationObjects, isValidValue);
        }
        else
        {
            result = result || isNotNullAndValid(validationObject, isValidValue);
        }

        return result;
    }

    private static <T> boolean isNullButOptional(T validationObject, boolean mandatory)
    {
        return validationObject == null && !mandatory;
    }

    private static <T> boolean isNotNullAndValid(T validationObject, Predicate<T> isValidValue)
    {
        return validationObject != null && isValidValue.test(validationObject);
    }

    private static <T> boolean isNotNullAndValid(Iterable<T> validationObjects, Predicate<T> isValidValue)
    {
        return Streams.sequential(validationObjects).allMatch(isValidValue);
    }
}
