package com.github.mizool.technology.gson;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.collect.ImmutableSet;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public abstract class AbstractTypeAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T>
{
    private final Iterable<ExclusionStrategy> exclusionStrategies;

    protected AbstractTypeAdapter(ExclusionStrategy... strategies)
    {
        exclusionStrategies = ImmutableSet.copyOf(strategies);
    }

    @Override
    public JsonElement serialize(T object, Type objectType, JsonSerializationContext context)
    {
        JsonObject result = null;
        if (object != null)
        {
            Collection<Field> declaredFields = getFields(object);
            result = serializeFields(object, declaredFields, context);
        }
        return result;
    }

    protected Set<Field> getFields(T object)
    {
        Set<Field> result = new HashSet<>();

        Class<?> objectClass = object.getClass();
        Field[] declaredFields = objectClass.getDeclaredFields();
        for (Field field : declaredFields)
        {
            if (!isExcluded(field))
            {
                result.add(field);
            }
        }

        return result;
    }

    protected boolean isExcluded(Field field)
    {
        boolean result = false;
        for (ExclusionStrategy strategy : exclusionStrategies)
        {
            if (strategy.shouldSkipField(new FieldAttributes(field)))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    protected JsonObject serializeFields(T object, Iterable<Field> fields, JsonSerializationContext context)
    {
        JsonObject result = new JsonObject();
        for (Field field : fields)
        {
            JsonElement jsonElement = serializeField(object, field, context);
            if (jsonElement != null)
            {
                result.add(field.getName(), jsonElement);
            }
        }
        return result;
    }

    protected JsonElement serializeField(T object, Field field, JsonSerializationContext context)
    {
        JsonElement result;
        Object fieldValue = getFieldValue(object, field);
        try
        {
            result = context.serialize(fieldValue);
        }
        catch (UnsupportedOperationException e)
        {
            throw new IllegalArgumentException("Could not serialize field " +
                field.getName() +
                " of class " +
                object.getClass()
                    .getCanonicalName(), e);
        }
        return result;
    }

    private Object getFieldValue(Object object, Field field)
    {
        try
        {
            field.setAccessible(true);
            return field.get(object);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Could not read field " + field.getName(), e);
        }
    }

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context)
    {
        T result = null;

        if (!json.isJsonNull())
        {
            JsonObject jsonObject = json.getAsJsonObject();
            result = instantiate(type);
            deserializeFields(result, jsonObject, context);
        }

        return result;
    }

    protected T instantiate(Type typeOfT)
    {
        @SuppressWarnings("unchecked") Class<T> resultClass = (Class<T>) typeOfT;
        T result;
        try
        {
            result = resultClass.getDeclaredConstructor()
                .newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new CodeInconsistencyException(e);
        }
        return result;
    }

    protected void deserializeFields(T object, JsonObject jsonObject, JsonDeserializationContext context)
    {
        Set<Field> fields = getFields(object);

        for (Field field : fields)
        {
            JsonElement jsonMemberValue = jsonObject.get(field.getName());
            deserializeField(object, field, jsonMemberValue, context);
        }
    }

    protected void deserializeField(
        T targetObject, Field field, JsonElement jsonValue, JsonDeserializationContext context)
    {
        Object fieldValue;
        try
        {
            fieldValue = context.deserialize(jsonValue, field.getGenericType());
        }
        catch (RuntimeException e)
        {
            throw new IllegalStateException("could not deserialize field " + field.getName() + " value " + jsonValue,
                e);
        }
        setFieldValue(targetObject, field, fieldValue);
    }

    protected void setFieldValue(T object, Field field, Object value)
    {
        field.setAccessible(true);
        try
        {
            field.set(object, value);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Could not write field " + field.getName(), e);
        }
    }
}