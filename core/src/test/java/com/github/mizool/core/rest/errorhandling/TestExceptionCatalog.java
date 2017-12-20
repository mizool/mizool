package com.github.mizool.core.rest.errorhandling;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

public class TestExceptionCatalog
{
    private ExceptionCatalog exceptionCatalog;

    @BeforeMethod
    public void setUp()
    {
        exceptionCatalog = new ExceptionCatalog();
    }

    @Test
    public void whitelisted()
    {
        FooException test = new FooException();
        Optional<WhiteListEntry> lookup = exceptionCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getExceptionClass()).isEqualTo(FooException.class);
    }

    @Test
    public void whitelistedBySuperclass()
    {
        BarException test = new BarException();
        Optional<WhiteListEntry> lookup = exceptionCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getExceptionClass()).isEqualTo(FooException.class);
    }

    @Test
    public void notWhitelisted()
    {
        QuuxException test = new QuuxException();
        Optional<WhiteListEntry> lookup = exceptionCatalog.lookup(test);
        assertThat(lookup.isPresent()).isFalse();
    }

    @Test
    public void favorsDirectlyWhitelisted()
    {
        MoepException test = new MoepException();
        Optional<WhiteListEntry> lookup = exceptionCatalog.lookup(test);
        assertThat(lookup.isPresent()).isTrue();
        assertThat(lookup.get().getExceptionClass()).isEqualTo(MoepException.class);
    }
}