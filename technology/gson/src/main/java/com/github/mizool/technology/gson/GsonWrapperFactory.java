package com.github.mizool.technology.gson;

import java.util.ServiceLoader;

import com.google.gson.GsonBuilder;

public class GsonWrapperFactory
{
    private static final Iterable<GsonBuilderListener>
        GSON_BUILDER_LISTENERS
        = ServiceLoader.load(GsonBuilderListener.class);

    public GsonWrapper create()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();

        registerTypeAdapters(gsonBuilder);

        return new GsonWrapper(gsonBuilder.create());
    }

    private void registerTypeAdapters(GsonBuilder gsonBuilder)
    {
        for (GsonBuilderListener gsonBuilderListener : GSON_BUILDER_LISTENERS)
        {
            gsonBuilderListener.onTypeAdapterRegistration(gsonBuilder);
        }
    }
}
