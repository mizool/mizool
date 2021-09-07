package com.github.mizool.core.st4;

import java.util.Locale;

import org.stringtemplate.v4.StringRenderer;

public class MizoolStringRenderer extends StringRenderer
{
    @Override
    public String toString(Object o, String formatString, Locale locale)
    {
        String s = (String) o;
        String result;

        if (formatString == null)
        {
            result = s;
        }
        else if (formatString.equals("uncap"))
        {
            if (s.length() > 0)
            {
                result = Character.toLowerCase(s.charAt(0)) + s.substring(1);
            }
            else
            {
                result = s;
            }
        }
        else
        {
            result = super.toString(o, formatString, locale);
        }

        return result;
    }
}
