package com.github.mizool.rest.errorhandling;

import javax.inject.Singleton;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
    private final ErrorHandler errorHandler;

    public ValidationExceptionMapper()
    {
        errorHandler = new ErrorHandler();
    }

    @Override
    public Response toResponse(ConstraintViolationException e)
    {
        ErrorResponse errorResponse = errorHandler.handle(e);
        Response result = Response.status(errorResponse.getStatusCode())
                                  .type(MediaType.APPLICATION_JSON_TYPE)
                                  .entity(errorResponse.getBody())
                                  .build();
        return result;
    }
}
