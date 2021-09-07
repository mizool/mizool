package com.github.mizool.core.configuration;

import java.net.MalformedURLException;
import java.net.URL;

import lombok.experimental.UtilityClass;

@UtilityClass
class UrlValues
{
    public URL parse(String value)
    {
        return parse(null, value);
    }

    public URL parse(URL context, String value)
    {
        try
        {
            return new URL(context, value);
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
