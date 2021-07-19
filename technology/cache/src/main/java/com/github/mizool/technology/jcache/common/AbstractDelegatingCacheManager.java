package com.github.mizool.technology.jcache.common;

import javax.cache.CacheManager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public abstract class AbstractDelegatingCacheManager implements CacheManager
{
    @Getter(AccessLevel.PROTECTED)
    @Setter
    @Delegate
    private CacheManager target;
}
