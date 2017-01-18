package com.github.mizool;

import java.util.ServiceLoader;

public class MetaInfServices
{
    public static <S> Iterable<S> instances(Class<S> service)
    {
        return ServiceLoader.load(service, service.getClassLoader());
    }
}
