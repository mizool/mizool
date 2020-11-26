package com.github.mizool.technology.gson;

import org.kohsuke.MetaInfServices;

import com.google.gson.GsonBuilder;

@MetaInfServices
public class FooGsonBuilderListener implements GsonBuilderListener
{
    @Override
    public void onTypeAdapterRegistration(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(Foo.class, new FooTypeAdapter());
    }
}