/**
 * Copyright 2017 incub8 Software Labs GmbH
 * Copyright 2017 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.technology.jackson;

import java.time.format.DateTimeParseException;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.kohsuke.MetaInfServices;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.mizool.core.rest.errorhandling.WhiteList;
import com.github.mizool.core.rest.errorhandling.WhiteListEntry;
import com.google.common.collect.ImmutableSet;

@MetaInfServices(WhiteList.class)
public class JacksonWhiteList implements WhiteList
{
    @Override
    public Set<WhiteListEntry> getEntries()
    {
        return ImmutableSet.<WhiteListEntry>builder().add(new WhiteListEntry(JsonParseException.class,
            HttpServletResponse.SC_BAD_REQUEST,
            true))
            .add(new WhiteListEntry(UnrecognizedPropertyException.class, HttpServletResponse.SC_BAD_REQUEST, true))
            .add(new WhiteListEntry(DateTimeParseException.class, HttpServletResponse.SC_BAD_REQUEST, true))
            .build();
    }
}