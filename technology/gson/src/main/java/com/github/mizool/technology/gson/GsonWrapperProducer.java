package com.github.mizool.technology.gson;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

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