package com.github.mizool.core;

import java.util.ServiceLoader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetaInfServices
{
    /**
     * @deprecated When it was introduced years ago, the point of this method was to ensure the {@link ServiceLoader}
     * call always used the classloader of the service class. At the time, this seemed to be important, even essential,
     * so it was decided to ensure this "correct" usage.
     *
     * <p>Fast-forward to 2025, this turned out to be harmful: in environments with layered classloaders like Quarkus,
     * this limits the returned service implementations to those from library jars and omits those supplied by the
     * application.
     *
     * <p>For this reason, this method now uses the current thread's context classloader. Consequently, calling it is
     * now equivalent to calling {@link ServiceLoader#load(Class)}. Calls should therefore be replaced as this method
     * is going to be removed.
     */
    @Deprecated(forRemoval = true, since = "8.1")
    public <S> Iterable<S> instances(Class<S> service)
    {
        return ServiceLoader.load(service);
    }
}
