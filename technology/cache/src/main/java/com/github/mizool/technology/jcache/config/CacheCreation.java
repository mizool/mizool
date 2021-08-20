package com.github.mizool.technology.jcache.config;

import javax.cache.configuration.MutableConfiguration;

import lombok.Data;

@Data
public class CacheCreation
{
    private final String cacheName;
    private final MutableConfiguration configuration;
}
