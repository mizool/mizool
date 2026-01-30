package com.github.mizool.technology.rest.errorhandling;

import java.util.Map;

public interface ParameterizedException
{
    Map<String, Object> getExceptionParameters();
}