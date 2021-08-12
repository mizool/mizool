package com.github.mizool.core.rest.errorhandling;

import java.util.Map;

public interface ParameterizedThrowable
{
    Map<String, Object> getExceptionParameters();
}