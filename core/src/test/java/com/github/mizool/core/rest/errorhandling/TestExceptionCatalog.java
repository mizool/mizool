package com.github.mizool.core.rest.errorhandling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestExceptionCatalog
{
    private ErrorHandlingCatalog errorHandlingCatalog;

    @BeforeMethod
    public void setUp()
    {
        errorHandlingCatalog = new ErrorHandlingCatalog();
    }

    @Test
    public void whitelisted()
    {
        FooException test = new FooException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getThrowableClass()).isEqualTo(FooException.class);
    }

    @Test
    public void whitelistedBySuperclass()
    {
        BarException test = new BarException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getThrowableClass()).isEqualTo(FooException.class);
    }

    @Test
    public void notWhitelisted()
    {
        QuuxException test = new QuuxException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingCatalog.lookup(test);
        assertThat(lookup.isPresent()).isFalse();
    }

    @Test
    public void favorsDirectlyWhitelisted()
    {
        MoepException test = new MoepException();
        Optional<ErrorHandlingBehavior> lookup = errorHandlingCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getThrowableClass()).isEqualTo(MoepException.class);
    }
}