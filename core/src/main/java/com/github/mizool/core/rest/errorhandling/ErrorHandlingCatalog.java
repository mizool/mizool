package com.github.mizool.core.rest.errorhandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import com.github.mizool.core.MetaInfServices;
import com.google.common.collect.ImmutableMap;

@Singleton
class ErrorHandlingCatalog
{
    private final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog;

    public ErrorHandlingCatalog()
    {
        Iterable<ErrorHandlingBehavior> behaviours = MetaInfServices.instances(ErrorHandlingBehavior.class);
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog = new HashMap<>();
        behaviours.forEach(behaviour -> {
            String behaviorPackage = behaviour.getClass().getPackage().getName();
            String corePackage = getClass().getPackage().getName();
            if (!catalog.containsKey(behaviour.getThrowableClass()) || !behaviorPackage.startsWith(corePackage))
            {
                catalog.put(behaviour.getThrowableClass(), behaviour);
            }
        });

        this.catalog = ImmutableMap.copyOf(catalog);
    }

    public Optional<ErrorHandlingBehavior> lookup(Throwable t)
    {
        ErrorHandlingBehavior behaviour = catalog.get(t.getClass());
        if (behaviour == null)
        {
            for (Class<? extends Throwable> throwableClass : catalog.keySet())
            {
                if (throwableClass.isAssignableFrom(t.getClass()))
                {
                    behaviour = catalog.get(throwableClass);
                }
            }
        }
        Optional<ErrorHandlingBehavior> result = Optional.ofNullable(behaviour);
        return result;
    }
}
