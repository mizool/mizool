package com.github.mizool.technology.gson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;

import jakarta.ws.rs.core.StreamingOutput;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonIOException;

public class TestGsonMessageBodyHandler
{
    private GsonMessageBodyHandler handler;
    private GsonWrapper gson;

    @BeforeMethod
    public void configure()
    {
        this.gson = mock(GsonWrapper.class);

        this.handler = new GsonMessageBodyHandler(gson);
    }

    @Test
    public void testReadFrom() throws IOException
    {
        Object expected = new Object();

        PredefinedReadResultGson customGson = new PredefinedReadResultGson(expected);
        GsonMessageBodyHandler customHandler = new GsonMessageBodyHandler(customGson);

        InputStream stream = new ByteArrayInputStream(new byte[]{ });

        Object actual = customHandler.readFrom(null, Object.class, null, null, null, stream);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * Works around the fact that {@link org.mockito.Mockito#when(Object)} did not work for {@link
     * com.github.mizool.technology.gson.TestGsonMessageBodyHandler#testReadFrom()} (either it didn't compile, or
     * the invocation arguments did not match at runtime).
     */
    private static class PredefinedReadResultGson extends GsonWrapper
    {
        private final Object fromJsonResult;

        public PredefinedReadResultGson(Object fromJsonResult)
        {
            super(null);
            this.fromJsonResult = fromJsonResult;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Object> T fromJson(Reader json, Type typeOfT)
        {
            return (T) this.fromJsonResult;
        }
    }

    @Test
    public void testWriteTo() throws IOException
    {
        WriteCapturingGson capturingGson = new WriteCapturingGson();
        GsonMessageBodyHandler customHandler = new GsonMessageBodyHandler(capturingGson);

        OutputStream stream = ByteStreams.nullOutputStream();
        String[] expected = new String[]{ "foo", "bar" };
        customHandler.writeTo(expected, null, expected.getClass(), null, null, null, stream);

        assertThat(capturingGson.writtenObject).isSameAs(expected);
        assertThat(capturingGson.writtenType).isSameAs(expected.getClass());
    }

    /**
     * Works around the fact that {@link org.mockito.Mockito#when(Object)} did not work for {@link
     * com.github.mizool.technology.gson.TestGsonMessageBodyHandler#testWriteTo() (either it didn't compile, or the
     * invocation arguments did not match at runtime).
     */
    private static class WriteCapturingGson extends GsonWrapper
    {
        public WriteCapturingGson()
        {
            super(null);
        }

        private Object writtenObject;
        private Type writtenType;

        @Override
        public void toJson(Object object, Type type, Appendable writer) throws JsonIOException
        {
            this.writtenObject = object;
            this.writtenType = type;
        }
    }

    @Test
    public void testGetSize()
    {
        long size = this.handler.getSize(null, null, null, null, null);
        assertThat(size).isEqualTo(-1);
    }

    @Test
    public void testIsReadable()
    {
        boolean readable = this.handler.isReadable(null, null, null, null);
        assertThat(readable).isTrue();
    }

    @Test
    public void testObjectsAreWriteable()
    {
        boolean writable = this.handler.isWriteable(Object.class, null, null, null);
        assertThat(writable).isTrue();
    }

    @Test
    public void testStreamingOutputIsNotWriteable()
    {
        boolean writable = this.handler.isWriteable(StreamingOutput.class, null, null, null);
        assertThat(writable).isFalse();
    }
}