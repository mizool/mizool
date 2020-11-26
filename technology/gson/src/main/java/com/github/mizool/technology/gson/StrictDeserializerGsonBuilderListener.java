package com.github.mizool.technology.gson;

import org.kohsuke.MetaInfServices;

import com.google.gson.GsonBuilder;

@MetaInfServices
public class StrictDeserializerGsonBuilderListener implements GsonBuilderListener
{
    @Override
    public void onTypeAdapterRegistration(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(Object.class, new StrictDeserializer());
    }
}