package com.github.mizool.core;

import java.util.ServiceLoader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetaInfServices
{
    public <S> Iterable<S> instances(Class<S> service)
    {
        return ServiceLoader.load(service, service.getClassLoader());
    }
}
