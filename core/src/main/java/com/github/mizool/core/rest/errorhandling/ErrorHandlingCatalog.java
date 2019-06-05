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
    private static final String CORE_PACKAGE = ErrorHandlingCatalog.class.getPackage().getName();

    private final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog;

    public ErrorHandlingCatalog()
    {
        Iterable<ErrorHandlingBehavior> behaviours = MetaInfServices.instances(ErrorHandlingBehavior.class);
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog = new HashMap<>();
        behaviours.forEach(behaviour -> {
            String behaviorPackage = behaviour.getClass().getPackage().getName();
            Class<? extends Throwable> throwableClass = behaviour.getThrowableClass();
            if (!catalog.containsKey(throwableClass) || !behaviorPackage.startsWith(CORE_PACKAGE))
            {
                catalog.put(throwableClass, behaviour);
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
