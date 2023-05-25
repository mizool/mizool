package com.github.mizool.core.rest.errorhandling;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ClientErrorException;

import lombok.extern.slf4j.Slf4j;

import com.github.mizool.core.exception.RuleViolationException;

@Slf4j
public class ErrorResponseFactory
{
    private final ConstraintViolationMapper constraintViolationMapper;
    private final RuleViolationMapper ruleViolationMapper;
    private final ClientErrorMapper clientErrorMapper;
    private final GenericErrorMapper genericErrorMapper;

    @Inject
    public ErrorResponseFactory(GlobalParametersSupplier globalParametersSupplier)
    {
        ruleViolationMapper = new RuleViolationMapper(globalParametersSupplier);
        clientErrorMapper = new ClientErrorMapper(globalParametersSupplier);
        genericErrorMapper = new GenericErrorMapper(globalParametersSupplier);
        constraintViolationMapper = new ConstraintViolationMapper(globalParametersSupplier);
    }

    public ErrorMessageDto fromPojo(Throwable throwable)
    {
        return this.handle(throwable)
            .getBody();
    }

    public ErrorResponse handle(Throwable throwable)
    {
        ErrorResponse result = null;

        Throwable cursor = throwable;
        while (cursor != null)
        {
            if (isAssignable(ConstraintViolationException.class, cursor))
            {
                result
                    = constraintViolationMapper.handleConstraintViolationError((ConstraintViolationException) cursor);
            }
            else if (isAssignable(RuleViolationException.class, cursor))
            {
                result = ruleViolationMapper.handleRuleViolationError((RuleViolationException) cursor);
            }
            else if (isAssignable(ClientErrorException.class, cursor))
            {
                result = clientErrorMapper.handleClientError((ClientErrorException) cursor);
            }
            else
            {
                result = genericErrorMapper.handleErrorAccordingToBehavior(cursor);
            }

            if (result != null)
            {
                break;
            }
            cursor = cursor.getCause();
        }

        if (result == null)
        {
            result = genericErrorMapper.handleUndefinedError(throwable);
        }

        return result;
    }

    private boolean isAssignable(Class<?> throwableClass, Throwable throwable)
    {
        return throwableClass.isAssignableFrom(throwable.getClass());
    }
}