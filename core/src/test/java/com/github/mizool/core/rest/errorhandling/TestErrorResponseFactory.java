package com.github.mizool.core.rest.errorhandling;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class TestErrorResponseFactory
{
    @Test
    public void producesResponseForFirstMatchInChain()
    {
        ErrorResponse errorResponse = new ErrorResponseFactory().handle(new WrappedException());

        assertThat(errorResponse.getStatusCode()).isEqualTo(new WrappedExceptionBehavior().getStatusCode());
        assertThat(errorResponse.getStatusCode()).isNotEqualTo(new OriginalExceptionBehavior().getStatusCode());
    }

    @Test
    public void addsGlobalParameters()
    {
        GlobalParametersSupplier globalParametersSupplier = () -> ImmutableMap.of("someKey", "someValue");

        ErrorResponse
            errorResponse
            = new ErrorResponseFactory(globalParametersSupplier).handle(new OriginalException());

        assertThat(errorResponse.getBody()
            .getGlobalParameters()).isEqualTo(ImmutableMap.of("someKey", "someValue"));
    }
}