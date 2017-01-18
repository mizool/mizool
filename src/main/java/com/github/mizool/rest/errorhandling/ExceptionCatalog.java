package com.github.mizool.rest.errorhandling;

import java.util.Map;

import javax.inject.Singleton;

import com.github.mizool.MetaInfServices;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@Singleton
class ExceptionCatalog
{
    private final Map<String, Integer> catalog;

    protected ExceptionCatalog()
    {
        Iterable<WhiteList> whiteLists = MetaInfServices.instances(WhiteList.class);

        ImmutableMap.Builder<String, Integer> catalogBuilder = ImmutableMap.builder();

        for (WhiteList whiteList : whiteLists)
        {
            catalogBuilder.putAll(whiteList.getEntries());
        }

        catalog = catalogBuilder.build();
    }

    public Optional<Integer> lookup(Throwable t)
    {
        String exceptionClassName = t.getClass().getName();
        Integer statusCode = catalog.get(exceptionClassName);
        Optional<Integer> result = Optional.fromNullable(statusCode);
        return result;
    }
}