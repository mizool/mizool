package com.github.mizool.technology.gson;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.github.mizool.core.exception.BadRequestException;
import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StrictDeserializer implements JsonDeserializer<Object>
{
    @Override
    public Object deserialize(JsonElement json, Type type, JsonDeserializationContext context)
    {
        Object result = null;

        if (!json.isJsonNull())
        {
            JsonObject jsonObject = json.getAsJsonObject();
            result = instantiate(type);
            deserializeFields(result, jsonObject, context);
        }

        return result;
    }

    private Object instantiate(Type type)
    {
        @SuppressWarnings("unchecked") Class<Object> resultClass = (Class<Object>) type;
        Object result;
        try
        {
            Constructor<Object> constructor = resultClass.getConstructor();
            constructor.setAccessible(true);
            result = constructor.newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new CodeInconsistencyException(e);
        }
        return result;
    }

    private void deserializeFields(Object targetObject, JsonObject sourceObject, JsonDeserializationContext context)
    {
        Map<String, Field> targetFields = getFields(targetObject);

        for (Map.Entry<String, JsonElement> entry : sourceObject.entrySet())
        {
            String fieldName = entry.getKey();

            if (!targetFields.containsKey(fieldName))
            {
                throw new BadRequestException(
                    targetObject.getClass().getName() +
                    " has no field named " +
                    fieldName);
            }

            JsonElement sourceValue = sourceObject.get(fieldName);
            deserializeField(targetObject, targetFields.get(fieldName), sourceValue, context);
        }
    }

    private Map<String, Field> getFields(Object object)
    {
        Map<String, Field> result = new HashMap<>();

        Class<? extends Object> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields)
        {
            result.put(field.getName(), field);
        }

        return result;
    }

    private void deserializeField(
        Object targetObject, Field targetField, JsonElement jsonValue, JsonDeserializationContext context)
    {
        Object fieldValue;
        try
        {
            fieldValue = context.deserialize(jsonValue, targetField.getGenericType());
        }
        catch (RuntimeException e)
        {
            throw new BadRequestException(
                "could not deserialize targetField " +
                targetField.getName() +
                " value " +
                jsonValue, e);
        }
        setFieldValue(targetObject, targetField, fieldValue);
    }

    private void setFieldValue(Object object, Field field, Object value)
    {
        field.setAccessible(true);
        try
        {
            field.set(object, value);
        }
        catch (IllegalAccessException e)
        {
            throw new CodeInconsistencyException("Could not write field " + field.getName(), e);
        }
    }
}