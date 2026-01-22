package com.github.mizool.core.rest.errorhandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableMap;
import jakarta.inject.Singleton;

@Singleton
@Slf4j
class ErrorHandlingBehaviorCatalog
{
    private static final String CORE_PACKAGE = ErrorHandlingBehaviorCatalog.class.getPackage()
        .getName();

    private final Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog;

    public ErrorHandlingBehaviorCatalog()
    {
        Iterable<ErrorHandlingBehavior> behaviors = ServiceLoader.load(ErrorHandlingBehavior.class);
        catalog = buildCatalog(behaviors);
    }

    private Map<Class<? extends Throwable>, ErrorHandlingBehavior> buildCatalog(
        Iterable<ErrorHandlingBehavior> behaviors)
    {
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> result = new HashMap<>();

        for (ErrorHandlingBehavior behavior : behaviors)
        {
            Class<? extends Throwable> throwableClass = behavior.getThrowableClass();
            String behaviorPackage = behavior.getClass()
                .getPackage()
                .getName();
            if (notInCatalog(result, throwableClass) || isApplicationLevel(behaviorPackage))
            {
                result.put(throwableClass, behavior);
            }
            else
            {
                log.debug("Ignoring behavior {}",
                    behavior.getClass()
                        .getName());
            }
        }

        return ImmutableMap.copyOf(result);
    }

    public Optional<ErrorHandlingBehavior> lookup(Throwable t)
    {
        ErrorHandlingBehavior behavior = catalog.get(t.getClass());
        if (behavior == null)
        {
            for (Map.Entry<Class<? extends Throwable>, ErrorHandlingBehavior> catalogEntry : catalog.entrySet())
            {
                if (catalogEntry.getKey()
                    .isAssignableFrom(t.getClass()))
                {
                    behavior = catalogEntry.getValue();
                }
            }
        }
        return Optional.ofNullable(behavior);
    }

    private boolean notInCatalog(
        Map<Class<? extends Throwable>, ErrorHandlingBehavior> catalog, Class<? extends Throwable> throwableClass)
    {
        return !catalog.containsKey(throwableClass);
    }

    private boolean isApplicationLevel(String behaviorPackage)
    {
        return !behaviorPackage.startsWith(CORE_PACKAGE);
    }
}
