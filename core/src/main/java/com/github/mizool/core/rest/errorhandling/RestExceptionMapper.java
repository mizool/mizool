package com.github.mizool.core.rest.errorhandling;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class RestExceptionMapper implements ExceptionMapper<Exception>
{
    @Inject
    private ErrorResponseFactory errorResponseFactory;

    @Override
    public Response toResponse(Exception e)
    {
        ErrorResponse errorResponse = errorResponseFactory.handle(e);
        Response result = Response.status(errorResponse.getStatusCode())
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(errorResponse.getBody())
            .build();
        return result;
    }
}
