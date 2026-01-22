package com.github.mizool.core.rest.errorhandling;

import java.util.Map;

public interface ParameterizedException
{
    Map<String, Object> getExceptionParameters();
}