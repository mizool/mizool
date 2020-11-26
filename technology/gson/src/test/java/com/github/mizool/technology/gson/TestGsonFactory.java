package com.github.mizool.technology.gson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.TypeAdapter;

public class TestGsonFactory
{
    private GsonWrapperFactory gsonWrapperFactory;

    @BeforeMethod
    public void setUp()
    {
        gsonWrapperFactory = new GsonWrapperFactory();
    }

    @Test
    public void createsGson()
    {
        GsonWrapper actual = gsonWrapperFactory.create();
        assertThat(actual).isNotNull();
    }

    @Test
    public void hasTestTypeAdapterRegistered()
    {
        GsonWrapper actual = gsonWrapperFactory.create();
        TypeAdapter<Foo> fooTypeAdapter = actual.getAdapter(Foo.class);
        assertThat(fooTypeAdapter).isNotNull();
    }
}