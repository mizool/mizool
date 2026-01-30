package com.github.mizool.technology.rest.errorhandling;

class WrappedException extends RuntimeException
{
    WrappedException()
    {
        super(new OriginalException());
    }
}