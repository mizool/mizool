package com.github.mizool.core.converter;

import lombok.NonNull;

public class EnumConverter
{
    public String fromPojo(Enum<?> enumValue)
    {
        String result = null;

        if (enumValue != null)
        {
            result = enumValue.name();
        }
        return result;
    }

    public <T extends Enum<T>> T toPojo(@NonNull Class<T> enumClass, String value)
    {
        T result = null;
        if (value != null)
        {
            result = Enum.valueOf(enumClass, value);
        }
        return result;
    }
}
