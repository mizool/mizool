package com.github.mizool.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestLazy
{
    private static class TestException extends RuntimeException
    {
    }

    @Test
    public void testRequiresSupplier()
    {
        Supplier<?> supplier = null;

        assertThatThrownBy(() -> new Lazy<>(supplier)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testSupplierIsNotCalledInConstructor()
    {
        Supplier<Integer> supplier = mockSupplier(null);

        new Lazy<>(supplier);

        verifyZeroInteractions(supplier);
    }

    @Test
    public void testExceptionsArePassedOn()
    {
        Supplier<Integer> supplier = mockSupplier();
        when(supplier.get()).thenThrow(TestException.class);

        Lazy<Integer> lazy = new Lazy<>(supplier);

        assertThatThrownBy(lazy::get).isInstanceOf(TestException.class);
    }

    @DataProvider
    protected Object[][] values()
    {
        return new Object[][]{
            { 42 }, { null }
        };
    }

    @Test(dataProvider = "values")
    public void testReturnsSuppliedValue(Integer value)
    {
        Supplier<Integer> supplier = mockSupplier(value);

        Lazy<Integer> lazy = new Lazy<>(supplier);

        Integer actual = lazy.get();

        assertThat(actual).isEqualTo(value);
    }

    @Test(dataProvider = "values")
    public void testReturnsTheSameValueForSubsequentCalls(Integer value)
    {
        Supplier<Integer> supplier = mockSupplier(value);

        Lazy<Integer> lazy = new Lazy<>(supplier);

        lazy.get();
        Integer actualOfSecondGetCall = lazy.get();

        assertThat(actualOfSecondGetCall).isSameAs(value);
    }

    @Test(dataProvider = "values")
    public void testSupplierIsOnlyCalledOnce(Integer value)
    {
        Supplier<Integer> supplier = mockSupplier(value);

        Lazy<Integer> lazy = new Lazy<>(supplier);

        lazy.get();
        lazy.get();

        verify(supplier, times(1)).get();
    }

    private Supplier<Integer> mockSupplier(Integer value)
    {
        Supplier<Integer> supplier = mockSupplier();
        when(supplier.get()).thenReturn(value);
        return supplier;
    }

    @SuppressWarnings("unchecked")
    private Supplier<Integer> mockSupplier()
    {
        return mock(Supplier.class);
    }
}