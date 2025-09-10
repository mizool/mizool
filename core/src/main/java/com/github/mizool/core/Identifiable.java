package com.github.mizool.core;

public interface Identifiable<T extends Identifiable<T>>
{
    Identifier<T> getId();
}
