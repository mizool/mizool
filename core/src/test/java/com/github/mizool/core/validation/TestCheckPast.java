/**
 * Copyright 2018 incub8 Software Labs GmbH
 * Copyright 2018 protel Hotelsoftware GmbH
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
package com.github.mizool.core.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCheckPast
{
    private static final ZonedDateTime NOW = ZonedDateTime.parse("2016-01-01T00:00:00Z[UTC]");

    private CheckPast checkPast;

    @BeforeMethod
    public void setUp()
    {
        checkPast = new CheckPast()
        {
            @Override
            protected ZonedDateTime now()
            {
                return NOW;
            }
        };
    }

    @Test(dataProvider = "dateTimeVariants")
    public void testsPast(String name, ZonedDateTime zonedDateTime, boolean expected)
    {
        boolean actual = checkPast.isValid(zonedDateTime, null);

        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider
    public Object[][] dateTimeVariants()
    {
        return new Object[][]{
            { "now", NOW, false },
            { "future", NOW.plus(1, ChronoUnit.MILLIS), false },
            { "past", NOW.minus(1, ChronoUnit.MILLIS), true }
        };
    }
}