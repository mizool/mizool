package com.github.mizool.technology.rest.errorhandling;

import java.util.Map;
import java.util.function.Supplier;

public interface GlobalParametersSupplier extends Supplier<Map<String, Object>>
{
}