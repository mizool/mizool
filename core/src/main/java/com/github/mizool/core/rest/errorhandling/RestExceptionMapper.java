package com.github.mizool.core.rest.errorhandling;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
