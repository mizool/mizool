package com.github.mizool.core.rest.errorhandling;

import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

@Slf4j
public class GenericErrorMapper
{
    static final String GLOBAL_PROPERTY_KEY = "GLOBAL";

    private final ErrorHandlingBehaviorCatalog errorHandlingBehaviorCatalog;
    private final GlobalParametersSupplier globalParametersSupplier;

    public GenericErrorMapper(GlobalParametersSupplier globalParametersSupplier)
    {
        this.globalParametersSupplier = globalParametersSupplier;

        errorHandlingBehaviorCatalog = new ErrorHandlingBehaviorCatalog();
    }

    public ErrorResponse handleErrorAccordingToBehavior(Throwable t)
    {
        Optional<ErrorHandlingBehavior> behaviorOptional = errorHandlingBehaviorCatalog.lookup(t);
        return behaviorOptional.map(behavior -> buildErrorResponse(t, behavior))
            .orElse(null);
    }

    public ErrorResponse handleUndefinedError(Throwable throwable)
    {
        log.error("Unhandled error", throwable);
        Map<String, Object> parameters = createExceptionParameters(throwable);

        ErrorDto error = ErrorDto.createGenericError(parameters);
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    private ErrorResponse buildErrorResponse(Throwable t, ErrorHandlingBehavior behavior)
    {
        logError(t, behavior);

        Map<String, Object> parameters = null;
        if (behavior.includeDetails())
        {
            parameters = createExceptionParameters(t);
        }

        ErrorDto error = ErrorDto.createGenericError(parameters);
        if (behavior.includeErrorId())
        {
            error = new ErrorDto(t.getClass()
                .getName(), parameters);
        }
        ErrorMessageDto errorMessage = createErrorMessageDto(error);
        return new ErrorResponse(behavior.getStatusCode(), errorMessage);
    }

    private void logError(Throwable t, ErrorHandlingBehavior behavior)
    {
        Throwable rootCause = determineRootCause(t);
        if (rootCause != t)
        {
            behavior.getMessageLogLevel()
                .log(log, "{} - {}", t.getMessage(), rootCause.getMessage());
        }
        else
        {
            behavior.getMessageLogLevel()
                .log(log, t.getMessage());
        }
        behavior.getStackTraceLogLevel()
            .log(log, t.getMessage(), t);
    }

    private Throwable determineRootCause(Throwable t)
    {
        Throwable rootCause = t;
        while (rootCause.getCause() != null)
        {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    private Map<String, Object> createExceptionParameters(Throwable throwable)
    {
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("Exception", throwable.getMessage());
        parameters.put("RootCause",
            Throwables.getRootCause(throwable)
                .getMessage());
        if (throwable instanceof ParameterizedException)
        {
            Map<String, Object> exceptionParameters = ((ParameterizedException) throwable).getExceptionParameters();
            if (exceptionParameters != null)
            {
                parameters.putAll(exceptionParameters);
            }
        }
        return parameters;
    }

    private ErrorMessageDto createErrorMessageDto(ErrorDto error)
    {
        SetMultimap<String, ErrorDto> errors = HashMultimap.create();
        errors.put(GLOBAL_PROPERTY_KEY, error);
        return ErrorMessageDto.builder()
            .errors(errors.asMap())
            .globalParameters(globalParametersSupplier.get())
            .build();
    }
}
