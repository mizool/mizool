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
package com.github.mizool.technology.jcache.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

public class CacheConfiguration
{
    private final Properties configuration = new Properties();
    private static final String CONFIG_FILE_NAME = "jcache.expiry.properties";

    public Configuration<Object, Object> getConfiguration(String cacheName)
    {
        MutableConfiguration<Object, Object> cacheConfiguration = new MutableConfiguration<>();
        if (configuration.isEmpty())
        {
            loadConfiguration();
        }

        if (configuration.containsKey(cacheName))
        {
            long timeToLive = Long.parseLong(configuration.getProperty(cacheName));
            Duration minutesToLive = new Duration(TimeUnit.MINUTES, timeToLive);
            cacheConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(minutesToLive));
        }

        return cacheConfiguration;
    }

    private void loadConfiguration()
    {
        try (InputStream cacheConfigFileStream = getClass().getResourceAsStream(CONFIG_FILE_NAME))
        {
            if (cacheConfigFileStream != null)
            {
                configuration.load(cacheConfigFileStream);
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}