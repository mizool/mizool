package com.github.mizool.core.configuration;

class SystemPropertyConfigurationImpl implements Configuration
{
    @Override
    public String get(String key)
    {
        return System.getProperty(key);
    }

    @Override
    public String get(String key, String defaultValue)
    {
        return System.getProperty(key, defaultValue);
    }
}
