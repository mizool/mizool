package com.github.mizool.core.exception;

import java.io.Serializable;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RuleViolation implements Serializable
{
    private final String fieldName;
    private final String errorId;
}
