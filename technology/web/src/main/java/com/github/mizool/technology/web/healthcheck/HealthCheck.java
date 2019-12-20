/**
 * Copyright 2019 incub8 Software Labs GmbH
 * Copyright 2019 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            result = ImmutableSet.of(new DefaultCheck());
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
        return checks.stream().map(Check::perform).collect(ImmutableSet.toImmutableSet());
    }

    private Response createResponse(Set<CheckResult> checkResults)
    {
        return getResponseBuilderWithStatus(checkResults).entity(getResponseBody(checkResults)).build();
    }

    Response.ResponseBuilder getResponseBuilderWithStatus(Set<CheckResult> checkResults)
    {
        Response.ResponseBuilder responseBuilder;
        if (checkResults.stream().allMatch(CheckResult::isSuccess))
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