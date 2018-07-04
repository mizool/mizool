package com.github.mizool.core.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestEnumConverter
{
    private enum Foo
    {
        MOEP
    }

    private EnumConverter converter;

    @BeforeMethod
    public void setUp()
    {
        converter = new EnumConverter();
    }

    @Test
    public void testFromPojo()
    {
        String actual = converter.fromPojo(Foo.MOEP);
        String expected = Foo.MOEP.name();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testToPojo()
    {
        Foo actual = converter.toPojo(Foo.class, "MOEP");
        Foo expected = Foo.MOEP;

        assertThat(actual).isEqualTo(expected);
    }
}