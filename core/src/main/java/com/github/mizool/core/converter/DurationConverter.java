package com.github.mizool.core.converter;

import java.time.Duration;

public class DurationConverter
{
    public String fromPojo(Duration value)
    {
        String result = null;
        if (value != null)
        {
            result = value.toString();
        }
        return result;
    }

    /**
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed to a duration
     */
    public Duration toPojo(String value)
    {
        Duration result = null;
        if (value != null)
        {
            result = Duration.parse(value);
        }
        return result;
    }
}
