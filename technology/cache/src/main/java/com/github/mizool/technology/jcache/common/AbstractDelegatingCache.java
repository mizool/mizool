package com.github.mizool.technology.jcache.common;

import javax.cache.Cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public abstract class AbstractDelegatingCache<K, V> implements Cache<K, V>
{
    @Getter(AccessLevel.PROTECTED)
    @Delegate
    private final Cache<K, V> target;
}
