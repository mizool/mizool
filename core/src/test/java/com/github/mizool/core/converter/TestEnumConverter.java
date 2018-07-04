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
package com.github.mizool.core.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestEnumConverter
{
    private enum Foo
    {
        MOEP
    }

    private EnumConverter converter;

    @BeforeMethod
    public void setUp()
    {
        converter = new EnumConverter();
    }

    @Test
    public void testFromPojo()
    {
        String actual = converter.fromPojo(Foo.MOEP);
        String expected = Foo.MOEP.name();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testToPojo()
    {
        Foo actual = converter.toPojo(Foo.class, "MOEP");
        Foo expected = Foo.MOEP;

        assertThat(actual).isEqualTo(expected);
    }
}