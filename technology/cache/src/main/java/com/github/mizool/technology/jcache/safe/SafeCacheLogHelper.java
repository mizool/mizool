package com.github.mizool.technology.jcache.safe;

import lombok.experimental.UtilityClass;

import org.slf4j.Logger;

import com.github.mizool.technology.jcache.timeouting.CacheTimeoutException;

@UtilityClass
class SafeCacheLogHelper
{
    public void onObtainManager(Exception e, Logger log)
    {
        log("cacheManager obtain failed", e, log);
    }

    public void onCreate(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache create failed", e, log);
    }

    public void onObtain(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache obtain failed", e, log);
    }

    public void onGet(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache get failed: " + key, e, log);
    }

    public void onPut(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache put failed: " + key, e, log);
    }

    public void onRemove(String cacheName, String key, Exception e, Logger log)
    {
        log(cacheName + " cache remove failed: " + key, e, log);
    }

    public void onRemoveAll(String cacheName, Exception e, Logger log)
    {
        log(cacheName + " cache remove all failed", e, log);
    }

    private Throwable rootCause(Throwable t)
    {
        Throwable result = t;
        while (result.getCause() != null)
        {
            result = result.getCause();
        }
        return result;
    }

    private void log(String message, Throwable t, Logger log)
    {
        if (t instanceof CacheTimeoutException)
        {
            t = rootCause(t);
            log.warn("{} - {}", message, t.getClass().getName());
            log.debug(message, t);
        }
        else
        {
            log.warn("{} - {}", message, t);
        }
    }
}
