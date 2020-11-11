package com.github.mizool.technology.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class FooTypeAdapter implements JsonSerializer<Foo>, JsonDeserializer<Foo>
{
    @Override
    public Foo deserialize(
        JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return null;
    }

    @Override
    public JsonElement serialize(Foo src, Type typeOfSrc, JsonSerializationContext context)
    {
        return null;
    }
}