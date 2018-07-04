package com.github.mizool.core.converter;

public class EnumConverter
{
    public String fromPojo(Enum<?> enumValue)
    {
        return enumValue.name();
    }

    public <T extends Enum<T>> T toPojo(Class<T> enumClass, String value)
    {
        return T.valueOf(enumClass, value);
    }
}