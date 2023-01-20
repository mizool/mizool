package com.github.mizool.technology.jcache.config;

import javax.cache.configuration.MutableConfiguration;

import lombok.Data;

@Data
public class CacheCreation<K, V>
{
    private final String cacheName;
    private final MutableConfiguration<K, V> configuration;
}
