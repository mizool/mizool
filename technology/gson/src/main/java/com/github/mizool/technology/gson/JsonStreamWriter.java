package com.github.mizool.technology.gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.google.gson.stream.JsonWriter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__(@Inject), access = AccessLevel.PROTECTED)
public class JsonStreamWriter implements MessageBodyWriter<Stream<?>>
{
    private final GsonWrapper gsonWrapper;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return Stream.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(
        Stream<?> stream, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo(
        Stream<?> stream,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
             JsonWriter jsonWriter = gsonWrapper.newJsonWriter(bufferedWriter);
             Stream<?> streamToClose = stream)
        {
            jsonWriter.beginArray();
            streamToClose.forEach(element -> gsonWrapper.toJson(element, element.getClass(), jsonWriter));
            jsonWriter.endArray();
        }
    }
}