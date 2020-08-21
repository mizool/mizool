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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;

import java.util.Optional;
import java.util.Properties;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.ConfigurationException;

public class TestValue
{
    private static final String KEY = "some.property";

    @Test
    public void testMissingKey()
    {
        Value<String> value = new Value<>(new Properties(), KEY, s -> fail("conversion was called for missing key"));

        assertThat(value.read()).isEmpty();
        assertThatThrownBy(value::obtain).isInstanceOf(ConfigurationException.class).hasMessageContaining(KEY);
    }

    @Test
    public void testConversionSuccess()
    {
        Properties properties = singletonProperty("The quick brown fox jumped over the lazy dog.");

        Value<Integer> value = new Value<>(properties, KEY, String::length);

        assertThat(value.read()).contains(45);
    }

    @Test
    public void testConversionFailure()
    {
        Properties properties = singletonProperty("41.9");

        // Instantiating the Value will not fail as the conversion was not called yet
        Value<Integer> value = new Value<>(properties, KEY, Integer::parseInt);

        Throwable throwable = catchThrowable(value::obtain);

        assertThat(throwable).isExactlyInstanceOf(ConfigurationException.class)
            .hasMessageContaining(KEY)
            .hasCauseExactlyInstanceOf(NumberFormatException.class);
    }

    @Test(dataProvider = "values")
    public void testValues(String remark, String rawPropertyValue, Optional<String> expected)
    {
        Properties properties = singletonProperty(rawPropertyValue);

        Value<String> value = new Value<>(properties, KEY, s -> s);

        assertThat(value.read()).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] values()
    {
        return new Object[][]{
            new Object[]{ "regular", "quux", Optional.of("quux") },
            new Object[]{ "empty", "", Optional.empty() },
            new Object[]{ "one space only", " ", Optional.of(" ") },
            new Object[]{ "trailing space", "foo ", Optional.of("foo ") },
            new Object[]{ "leading space", " bar", Optional.of(" bar") },
            new Object[]{ "leading & trailing space", " foobar ", Optional.of(" foobar ") }
        };
    }

    private Properties singletonProperty(String value)
    {
        Properties properties = new Properties();
        properties.setProperty(KEY, value);
        return properties;
    }
}
