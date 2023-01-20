package com.github.mizool.technology.web.healthcheck;

import java.util.Set;

import javax.ws.rs.core.Response;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class HealthCheck
{
    public Response check(Check... checks)
    {
        return check(asSetOrDefault(checks));
    }

    private Set<Check> asSetOrDefault(Check[] checks)
    {
        Set<Check> result;
        if (checks.length == 0)
        {
            result = Set.of(new DefaultCheck());
        }
        else
        {
            result = ImmutableSet.copyOf(checks);
        }
        return result;
    }

    private Response check(Set<Check> checks)
    {
        Set<CheckResult> checkResults = performChecks(checks);
        return createResponse(checkResults);
    }

    private Set<CheckResult> performChecks(Set<Check> checks)
    {
        return checks.stream()
            .map(Check::perform)
            .collect(ImmutableSet.toImmutableSet());
    }

    private Response createResponse(Set<CheckResult> checkResults)
    {
        return getResponseBuilderWithStatus(checkResults).entity(getResponseBody(checkResults))
            .build();
    }

    Response.ResponseBuilder getResponseBuilderWithStatus(Set<CheckResult> checkResults)
    {
        Response.ResponseBuilder responseBuilder;
        if (checkResults.stream()
            .allMatch(CheckResult::isSuccess))
        {
            responseBuilder = Response.ok();
        }
        else
        {
            responseBuilder = Response.status(Response.Status.SERVICE_UNAVAILABLE);
        }
        return responseBuilder;
    }

    private Object getResponseBody(Set<CheckResult> checkResults)
    {
        return checkResults.stream()
            .collect(ImmutableMap.toImmutableMap(CheckResult::getName, CheckResult::getMessage));
    }
}
