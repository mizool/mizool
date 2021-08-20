package com.github.mizool.core.converter;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlConverter
{
    public String fromPojo(URL value)
    {
        String result = null;

        if (value != null)
        {
            result = value.toExternalForm();
        }
        return result;
    }

    public URL toPojo(String value)
    {
        URL result = null;
        if (value != null)
        {
            try
            {
                result = new URL(value);
            }
            catch (MalformedURLException e)
            {
                throw new UncheckedIOException(e);
            }
        }
        return result;
    }
}
