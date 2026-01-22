package com.github.mizool.core.rest.errorhandling;

import java.util.Map;
import java.util.function.Supplier;

public interface GlobalParametersSupplier extends Supplier<Map<String, Object>>
{
}