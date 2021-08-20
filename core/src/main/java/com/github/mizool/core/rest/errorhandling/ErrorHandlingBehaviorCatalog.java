package com.github.mizool.core.rest.errorhandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.MetaInfServices;
import com.google.common.collect.ImmutableMap;

@Singleton
@Slf4j
class ErrorHandlingBehaviorCatalog
{
    private static final String CORE_PACKAGE = ErrorHandlingBehaviorCatalog.class.getPackage().getName();

    private final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog;

    public ErrorHandlingBehaviorCatalog()
    {
        Iterable<ErrorHandlingBehavior> behaviors = MetaInfServices.instances(ErrorHandlingBehavior.class);
        final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog = new HashMap<>();
        behaviors.forEach(behavior -> {
            String behaviorPackage = behavior.getClass().getPackage().getName();
            Class<? extends Throwable> throwableClass = behavior.getThrowableClass();
            if (notInCatalog(catalog, throwableClass) || isApplicationLevel(behaviorPackage))
            {
                catalog.put(throwableClass, behavior);
            }
            else
            {
                log.debug("Ignoring behavior {}", behavior.getClass().getName());
            }
        });

        this.catalog = ImmutableMap.copyOf(catalog);
    }

    public Optional<ErrorHandlingBehavior> lookup(Throwable t)
    {
        ErrorHandlingBehavior behavior = catalog.get(t.getClass());
        if (behavior == null)
        {
            for (Class<? extends Throwable> throwableClass : catalog.keySet())
            {
                if (throwableClass.isAssignableFrom(t.getClass()))
                {
                    behavior = catalog.get(throwableClass);
                }
            }
        }
        Optional<ErrorHandlingBehavior> result = Optional.ofNullable(behavior);
        return result;
    }

    private boolean notInCatalog(
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog,
        Class<? extends Throwable> throwableClass)
    {
        return !catalog.containsKey(throwableClass);
    }

    private boolean isApplicationLevel(String behaviorPackage)
    {
        return !behaviorPackage.startsWith(CORE_PACKAGE);
    }
}
