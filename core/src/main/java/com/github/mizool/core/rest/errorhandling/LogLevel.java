package com.github.mizool.core.rest.errorhandling;

import org.slf4j.Logger;
import org.slf4j.Marker;

public enum LogLevel
{
    NONE,
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR;

    public void log(Logger log, String msg)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(msg);
                break;
            case DEBUG:
                log.debug(msg);
                break;
            case INFO:
                log.info(msg);
                break;
            case WARN:
                log.warn(msg);
                break;
            case ERROR:
                log.error(msg);
                break;
        }
    }

    public void log(Logger log, String format, Object arg)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(format, arg);
                break;
            case DEBUG:
                log.debug(format, arg);
                break;
            case INFO:
                log.info(format, arg);
                break;
            case WARN:
                log.warn(format, arg);
                break;
            case ERROR:
                log.error(format, arg);
                break;
        }
    }

    public void log(Logger log, String format, Object arg1, Object arg2)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(format, arg1, arg2);
                break;
            case DEBUG:
                log.debug(format, arg1, arg2);
                break;
            case INFO:
                log.info(format, arg1, arg2);
                break;
            case WARN:
                log.warn(format, arg1, arg2);
                break;
            case ERROR:
                log.error(format, arg1, arg2);
                break;
        }
    }

    public void log(Logger log, String format, Object... arguments)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(format, arguments);
                break;
            case DEBUG:
                log.debug(format, arguments);
                break;
            case INFO:
                log.info(format, arguments);
                break;
            case WARN:
                log.warn(format, arguments);
                break;
            case ERROR:
                log.error(format, arguments);
                break;
        }
    }

    public void log(Logger log, String msg, Throwable t)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(msg, t);
                break;
            case DEBUG:
                log.debug(msg, t);
                break;
            case INFO:
                log.info(msg, t);
                break;
            case WARN:
                log.warn(msg, t);
                break;
            case ERROR:
                log.error(msg, t);
                break;
        }
    }

    public void log(Logger log, Marker marker, String msg)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(marker, msg);
                break;
            case DEBUG:
                log.debug(marker, msg);
                break;
            case INFO:
                log.info(marker, msg);
                break;
            case WARN:
                log.warn(marker, msg);
                break;
            case ERROR:
                log.error(marker, msg);
                break;
        }
    }

    public void log(Logger log, Marker marker, String format, Object arg)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(marker, format, arg);
                break;
            case DEBUG:
                log.debug(marker, format, arg);
                break;
            case INFO:
                log.info(marker, format, arg);
                break;
            case WARN:
                log.warn(marker, format, arg);
                break;
            case ERROR:
                log.error(marker, format, arg);
                break;
        }
    }

    public void log(Logger log, Marker marker, String format, Object arg1, Object arg2)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(marker, format, arg1, arg2);
                break;
            case DEBUG:
                log.debug(marker, format, arg1, arg2);
                break;
            case INFO:
                log.info(marker, format, arg1, arg2);
                break;
            case WARN:
                log.warn(marker, format, arg1, arg2);
                break;
            case ERROR:
                log.error(marker, format, arg1, arg2);
                break;
        }
    }

    public void log(Logger log, Marker marker, String format, Object... arguments)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(marker, format, arguments);
                break;
            case DEBUG:
                log.debug(marker, format, arguments);
                break;
            case INFO:
                log.info(marker, format, arguments);
                break;
            case WARN:
                log.warn(marker, format, arguments);
                break;
            case ERROR:
                log.error(marker, format, arguments);
                break;
        }
    }

    public void log(Logger log, Marker marker, String msg, Throwable t)
    {
        switch (this)
        {
            case NONE:
                break;
            case TRACE:
                log.trace(marker, msg, t);
                break;
            case DEBUG:
                log.debug(marker, msg, t);
                break;
            case INFO:
                log.info(marker, msg, t);
                break;
            case WARN:
                log.warn(marker, msg, t);
                break;
            case ERROR:
                log.error(marker, msg, t);
                break;
        }
    }
}
