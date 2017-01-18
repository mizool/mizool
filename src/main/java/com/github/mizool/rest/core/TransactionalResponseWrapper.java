package com.github.mizool.rest.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class TransactionalResponseWrapper extends HttpServletResponseWrapper
{
    private final ByteArrayOutputStream outputStream;
    private final HttpServletResponse response;

    private PrintWriter writer;

    public TransactionalResponseWrapper(HttpServletResponse response)
    {
        super(response);
        this.response = response;
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException
    {
        return new ServletOutputStream()
        {
            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener)
            {
                try
                {
                    writeListener.onWritePossible();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void write(int b) throws IOException
            {
                outputStream.write(b);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException
    {
        if (writer == null)
        {
            writer = new PrintWriter(
                new OutputStreamWriter(
                    outputStream, Charset.forName(response.getCharacterEncoding())));
        }
        return writer;
    }

    public void commit() throws IOException
    {
        response.getOutputStream().write(outputStream.toByteArray());
    }
}