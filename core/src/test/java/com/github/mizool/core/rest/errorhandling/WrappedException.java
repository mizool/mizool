package com.github.mizool.core.rest.errorhandling;

class WrappedException extends RuntimeException
{
    WrappedException()
    {
        super(new OriginalException());
    }
}