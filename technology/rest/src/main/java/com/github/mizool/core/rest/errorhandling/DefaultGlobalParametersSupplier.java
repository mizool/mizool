package com.github.mizool.core.rest.errorhandling;

import java.util.Collections;
import java.util.Map;

/**
 * Provides a noop implementation of the {@link GlobalParametersSupplier} to satisfy injection in JavaEE applications.
 * To add global parameters in a JavaEE application, extend this class and use the
 * {@link jakarta.enterprise.inject.Specializes} annotation.
 */
public class DefaultGlobalParametersSupplier implements GlobalParametersSupplier
{
    @Override
    public Map<String, Object> get()
    {
        return Collections.emptyMap();
    }
}