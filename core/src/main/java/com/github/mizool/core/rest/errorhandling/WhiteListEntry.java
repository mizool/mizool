package com.github.mizool.core.rest.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WhiteListEntry
{
    @Getter
    private final Class<? extends Exception> exceptionClass;

    @Getter
    private final int statusCode;

    private final boolean shouldIncludeDetails;

    public WhiteListEntry(Class<? extends Exception> exceptionClass, int statusCode)
    {
        this.exceptionClass = exceptionClass;
        this.statusCode = statusCode;
        this.shouldIncludeDetails = false;
    }

    public boolean getShouldIncludeDetails()
    {
        return shouldIncludeDetails;
    }
}