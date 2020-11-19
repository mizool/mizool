package com.github.mizool.technology.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import lombok.RequiredArgsConstructor;

import com.google.common.annotations.VisibleForTesting;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
// constructor is visible for smoke tests until it uses injection brought by MJX-1026
@VisibleForTesting
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object>
{
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final GsonWrapper gsonWrapper;

    @Override
    public boolean isReadable(
        Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return true;
    }

    @Override
    public Object readFrom(
        Class<Object> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream) throws IOException
    {
        return readFrom(entityStream, genericType);
    }

    private Object readFrom(InputStream entityStream, Type type) throws IOException
    {
        ensureInstantiationPossible(type);
        try (InputStreamReader streamReader = new InputStreamReader(entityStream, CHARSET))
        {
            return gsonWrapper.fromJson(streamReader, type);
        }
    }

    private void ensureInstantiationPossible(Type type)
    {
        if (type instanceof Class)
        {
            Class<?> c = (Class<?>) type;
            if (c.isPrimitive())
            {
                throw new IllegalArgumentException("cannot deserialize primitive types (here: '" + c.getName() + "')");
            }
            else if (c.isInterface())
            {
                throw new IllegalArgumentException("cannot deserialize stream to type '" +
                    c.getName() +
                    "' because it is an interface");
            }
            else if (Modifier.isAbstract(c.getModifiers()))
            {
                throw new IllegalArgumentException("cannot deserialize stream to type '" +
                    c.getName() +
                    "' because it is abstract");
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        // TODO MJX-806: Marker interface will make this implicit
        return !StreamingOutput.class.isAssignableFrom(type) && !Stream.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(
        Object object,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream) throws IOException
    {
        writeTo(entityStream, object, genericType);
    }

    private void writeTo(OutputStream entityStream, Object object, Type type) throws IOException
    {
        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, CHARSET))
        {
            gsonWrapper.toJson(object, type, writer);
        }
    }
}