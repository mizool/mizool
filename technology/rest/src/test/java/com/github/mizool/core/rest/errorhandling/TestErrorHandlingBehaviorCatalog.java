package com.github.mizool.core.rest.errorhandling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestErrorHandlingBehaviorCatalog
{
    private ErrorHandlingBehaviorCatalog errorHandlingBehaviorCatalog;

    @BeforeMethod
    public void setUp()
    {
        errorHandlingBehaviorCatalog = new ErrorHandlingBehaviorCatalog();
    }

    @Test
    public void whitelisted()
    {
        FooException test = new FooException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingBehaviorCatalog.lookup(test);
        assertThat(lookup).isPresent();
        assertThat(lookup.get()
            .getThrowableClass()).isEqualTo(FooException.class);
    }

    @Test
    public void whitelistedBySuperclass()
    {
        BarException test = new BarException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingBehaviorCatalog.lookup(test);
        assertThat(lookup).isPresent();
        assertThat(lookup.get()
            .getThrowableClass()).isEqualTo(FooException.class);
    }

    @Test
    public void notWhitelisted()
    {
        QuuxException test = new QuuxException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingBehaviorCatalog.lookup(test);
        assertThat(lookup).isEmpty();
    }

    @Test
    public void favorsDirectlyWhitelisted()
    {
        MoepException test = new MoepException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingBehaviorCatalog.lookup(test);
        assertThat(lookup).isPresent();
        assertThat(lookup.get()
            .getThrowableClass()).isEqualTo(MoepException.class);
    }
}