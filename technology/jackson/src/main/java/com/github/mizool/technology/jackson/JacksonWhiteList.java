package com.github.mizool.technology.jackson;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.mizool.core.rest.errorhandling.WhiteList;
import com.github.mizool.core.rest.errorhandling.WhiteListEntry;
import com.google.common.collect.ImmutableSet;

public class JacksonWhiteList implements WhiteList
{
    @Override
    public Set<WhiteListEntry> getEntries()
    {
        return ImmutableSet.<WhiteListEntry>builder()
            .add(new WhiteListEntry(JsonParseException.class.getName(), HttpServletResponse.SC_BAD_REQUEST, true))
            .add(
                new WhiteListEntry(
                    UnrecognizedPropertyException.class.getName(), HttpServletResponse.SC_BAD_REQUEST, true))
            .build();
    }
}