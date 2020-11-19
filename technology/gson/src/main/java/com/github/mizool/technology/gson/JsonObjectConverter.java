package com.github.mizool.technology.gson;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonObjectConverter
{
    public String toPojo(JsonObject dto)
    {
        String pojo = null;

        if (dto != null)
        {
            pojo = dto.toString();
        }

        return pojo;
    }

    public JsonObject fromPojo(String pojo)
    {
        JsonObject dto = null;

        if (pojo != null)
        {
            dto = new JsonParser().parse(pojo).getAsJsonObject();
        }

        return dto;
    }
}