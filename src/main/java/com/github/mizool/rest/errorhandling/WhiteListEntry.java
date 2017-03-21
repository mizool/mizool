package com.github.mizool.rest.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WhiteListEntry
{
    @Getter
    private final String exceptionClassName;

    @Getter
    private final int statusCode;

    private final boolean shouldIncludeDetails;

    public WhiteListEntry(String exceptionClassName, int statusCode)
    {
        this.exceptionClassName = exceptionClassName;
        this.statusCode = statusCode;
        this.shouldIncludeDetails = false;
    }

    public boolean getShouldIncludeDetails()
    {
        return shouldIncludeDetails;
    }
}