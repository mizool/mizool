package com.github.mizool.technology.gson.time;

import java.time.ZonedDateTime;

import org.kohsuke.MetaInfServices;

import com.github.mizool.technology.gson.GsonBuilderListener;
import com.google.gson.GsonBuilder;

@MetaInfServices
public class TimeGsonBuilderListener implements GsonBuilderListener
{
    @Override
    public void onTypeAdapterRegistration(GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter());
    }
}