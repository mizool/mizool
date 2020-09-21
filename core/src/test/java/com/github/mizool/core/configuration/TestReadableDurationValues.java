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

import java.time.Duration;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestReadableDurationValues
{
    @Test(dataProvider = "values")
    public void testValues(String string, Duration expected)
    {
        assertThat(ReadableDurationValues.parse(string)).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] values()
    {
        return new Object[][]{
            new Object[]{ "878342001 nanos", Duration.ofNanos(878342001) },
            new Object[]{ "391931 micros", Duration.ofNanos(391931000) },
            new Object[]{ "200 millis", Duration.ofMillis(200) },
            new Object[]{ "30 seconds", Duration.ofSeconds(30) },
            new Object[]{ "5 minutes", Duration.ofMinutes(5) },
            new Object[]{ "1 hours", Duration.ofHours(1) },
            new Object[]{ "7 days", Duration.ofDays(7) }
        };
    }
}
