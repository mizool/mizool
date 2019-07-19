package com.github.mizool.core.configuration;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Strings;

@Vetoed
public class EnvironmentVariableConfigurationImpl implements Configuration
{
    @Override
    public String get(String key)
    {
        return System.getenv(key);
    }

    @Override
    public String get(String key, String def)
    {
        String result = System.getenv(key);
        if (Strings.isNullOrEmpty(result))
        {
            result = def;
        }
        return result;
    }
}
