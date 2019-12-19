package com.github.mizool.technology.web.healthcheck;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CheckResult
{
    private final String name;
    private final boolean success;
    private final String message;
}