package com.github.mizool.core.configuration;

public interface Configuration
{
    String get(String key);

    String get(String key, String def);
}
