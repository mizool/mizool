package com.github.mizool.core.rest.errorhandling;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * We need this specialized ExceptionMapper for ConstraintViolation in order to prevent the jersey bean validation
 * from using its own ValidationExceptionMapper.
 * See <a href="https://stackoverflow.com/questions/38681986/how-to-override-a-built-in-exception-mapper-in-jersey-2-23#comment64768295_38681986">here</a> for more details.
 */
@Provider
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException>
{
    private final RestExceptionMapper restExceptionMapper;

    @Override
    public Response toResponse(ConstraintViolationException exception)
    {
        return restExceptionMapper.toResponse(exception);
    }
}
