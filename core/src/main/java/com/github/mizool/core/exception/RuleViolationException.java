/**
 * Copyright 2017 incub8 Software Labs GmbH
 * Copyright 2017 protel Hotelsoftware GmbH
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