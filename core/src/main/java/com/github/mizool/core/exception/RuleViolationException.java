package com.github.mizool.core.exception;

import java.util.Set;

import lombok.NonNull;

import com.google.common.collect.ImmutableSet;

public class RuleViolationException extends RuntimeException
{
    private final ImmutableSet<RuleViolation> ruleViolations;

    public RuleViolationException(String message, @NonNull Set<RuleViolation> ruleViolations)
    {
        super(message);
        this.ruleViolations = ImmutableSet.copyOf(ruleViolations);
    }

    public RuleViolationException(Set<RuleViolation> ruleViolations)
    {
        this(null, ruleViolations);
    }

    public Set<RuleViolation> getRuleViolations()
    {
        return ruleViolations;
    }
}
