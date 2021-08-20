package com.github.mizool.core.converter;

import com.github.mizool.core.Identifiable;
import com.github.mizool.core.Identifier;

public class IdentifierConverter
{
    public String fromPojo(Identifier<?> pojo)
    {
        String result = null;

        if (pojo != null)
        {
            result = pojo.getValue();
        }

        return result;
    }

    public <T extends Identifiable<T>> Identifier<T> toPojo(String value, Class<T> pojoClass)
    {
        Identifier<T> pojo = null;

        if (value != null)
        {
            pojo = Identifier.forPojo(pojoClass).of(value);
        }

        return pojo;
    }
}
