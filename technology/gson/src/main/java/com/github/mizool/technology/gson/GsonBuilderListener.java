package com.github.mizool.technology.gson;

import com.google.gson.GsonBuilder;

public interface GsonBuilderListener
{
    void onTypeAdapterRegistration(GsonBuilder gsonBuilder);
}