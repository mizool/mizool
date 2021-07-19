package com.github.mizool.core.concurrent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
class ExceptionTests
{
    public Throwable instantiateThrowable(Class<? extends Throwable> throwableClass, String message)
    {
        try
        {
            return getStringArgConstructor(throwableClass).newInstance(message);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new CodeInconsistencyException("Could not instantiate " + throwableClass.getCanonicalName(), e);
        }
    }

    private <T> Constructor<T> getStringArgConstructor(Class<T> theClass)
    {
        try
        {
            return theClass.getConstructor(String.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new CodeInconsistencyException("Could not find String arg constructor for " +
                theClass.getCanonicalName(), e);
        }
    }
}
