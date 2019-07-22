package com.github.mizool.core.configuration;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class EnvironmentVariableConfigurationImpl implements Configuration
{
    @Override
    public String get(String key)
    {
        return System.getenv(key);
    }

    @Override
    public String get(String key, String defaultValue)
    {
        String result = System.getenv(key);
        if (result != null)
        {
            result = defaultValue;
        }
        return result;
    }
}
