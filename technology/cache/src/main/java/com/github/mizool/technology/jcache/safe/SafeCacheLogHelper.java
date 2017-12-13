package com.github.mizool.technology.jcache.safe;

import lombok.experimental.UtilityClass;

import org.slf4j.Logger;

@UtilityClass
class SafeCacheLogHelper
{
    public void log(String message, Exception e, Logger log)
    {
        log.warn("{} - {}", message, e.getClass().getName());
        log.debug(message, e);
    }
}