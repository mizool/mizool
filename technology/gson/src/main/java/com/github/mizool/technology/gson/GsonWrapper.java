package com.github.mizool.technology.gson;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import com.google.gson.Gson;

/**
 * Allows code using Gson to be tested. Unlike the final {@link com.google.gson.Gson} class, this class can be mocked
 * easily.
 */
@RequiredArgsConstructor
public class GsonWrapper
{
    @Delegate
    private final Gson target;
}
