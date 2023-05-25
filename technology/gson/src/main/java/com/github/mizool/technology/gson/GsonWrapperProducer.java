package com.github.mizool.technology.gson;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

public class GsonWrapperProducer
{
    private final GsonWrapperFactory gsonWrapperFactory;

    @Inject
    public GsonWrapperProducer(GsonWrapperFactory gsonWrapperFactory)
    {
        this.gsonWrapperFactory = gsonWrapperFactory;
    }

    @Produces
    @Singleton
    public GsonWrapper produce()
    {
        return gsonWrapperFactory.create();
    }
}