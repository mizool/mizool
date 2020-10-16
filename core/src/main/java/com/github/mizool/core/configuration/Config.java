/*
 * Copyright 2020 incub8 Software Labs GmbH
 * Copyright 2020 protel Hotelsoftware GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mizool.core.configuration;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

import lombok.experimental.UtilityClass;

/**
 * Main entry point for the configuration API.
 */
@UtilityClass
public class Config
{
    /**
     * Creates a root node without any properties. Usually used as a starting point followed by multiple {@code
     * RootNode.add()} calls.
     *
     * @see RootNode#add(Reader)
     * @see RootNode#add(InputStream, Charset)
     */
    public RootNode blank()
    {
        return RootNode.createFrom(new Properties());
    }

    /**
     * Creates a root node encapsulating access to the current system properties.
     */
    public RootNode systemProperties()
    {
        return RootNode.createFrom(System.getProperties());
    }

    /**
     * Creates a root node encapsulating access to the given {@link Properties} instance.
     */
    public RootNode from(Properties properties)
    {
        return RootNode.createFrom(properties);
    }
}
