package com.github.mizool.core.rest.errorhandling;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestErrorResponseFactory
{
    private ErrorResponseFactory errorResponseFactory;

    @BeforeMethod
    public void setUp()
    {
        errorResponseFactory = new ErrorResponseFactory();
    }

    @Test
    public void producesResponseForFirstMatchInChain()
    {
        ErrorResponse errorResponse = errorResponseFactory.handle(new WrappedException());
        assertThat(errorResponse.getStatusCode()).isEqualTo(new WrappedExceptionBehavior().getStatusCode());
        assertThat(errorResponse.getStatusCode()).isNotEqualTo(new OriginalExceptionBehavior().getStatusCode());
    }
}
