/**
 * Copyright 2017-2018 incub8 Software Labs GmbH
 * Copyright 2017-2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.rest.errorhandling;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.RuleViolationException;
import com.google.common.base.Optional;

@Slf4j
public class ErrorHandler
{
    private final ExceptionCatalog exceptionCatalog;
    private final ErrorMapper errorMapper;
    private final ConstraintViolationMapper constraintViolationMapper;
    private final RuleViolationMapper ruleViolationMapper;

    public ErrorHandler()
    {
        this.exceptionCatalog = new ExceptionCatalog();
        errorMapper = new ErrorMapper();
        constraintViolationMapper = new ConstraintViolationMapper();
        ruleViolationMapper = new RuleViolationMapper();
    }

    @Inject
    protected ErrorHandler(ExceptionCatalog exceptionCatalog)
    {
        this.exceptionCatalog = exceptionCatalog;
        errorMapper = new ErrorMapper();
        constraintViolationMapper = new ConstraintViolationMapper();
        ruleViolationMapper = new RuleViolationMapper();
    }

    public ErrorMessageDto fromPojo(Throwable throwable)
    {
        return this.handle(throwable).getBody();
    }

    public ErrorResponse handle(Throwable throwable)
    {
        ErrorResponse result = null;

        Throwable cursor = throwable;
        while (cursor != null)
        {
            Optional<WhiteListEntry> whiteListEntry = exceptionCatalog.lookup(cursor);
            if (whiteListEntry.isPresent())
            {
                result = errorMapper.handleWhitelistedException(cursor, whiteListEntry.get());
                break;
            }
            else if (isAssignable(ConstraintViolationException.class, cursor))
            {
                result = constraintViolationMapper.handleConstraintViolationError((ConstraintViolationException) cursor);
            }
            else if (isAssignable(RuleViolationException.class, cursor))
            {
                result = ruleViolationMapper.handleRuleViolationError((RuleViolationException) cursor);
            }
            else if (isAssignable(ClientErrorException.class, cursor))
            {
                result = errorMapper.handleClientError((ClientErrorException) cursor);
            }
            cursor = cursor.getCause();
        }

        if (result == null)
        {
            result = errorMapper.handleUndefinedError(throwable);
        }

        return result;
    }

    private boolean isAssignable(Class<?> throwableClass, Throwable throwable)
    {
        return throwableClass.isAssignableFrom(throwable.getClass());
    }
}