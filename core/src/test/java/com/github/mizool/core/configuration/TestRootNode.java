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

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.testng.annotations.Test;

public class TestRootNode
{
    @Test
    public void testAdd()
    {
        RootNode rootNode = Config.blank()
            .add(getClass().getResourceAsStream("TestRootNode.properties"), StandardCharsets.UTF_8);

        String value = rootNode.child("content").stringValue().obtain();

        assertThat(value).isEqualTo("nonsense");
    }

    @Test
    public void testAddDoesNotAlterOriginal()
    {
        Properties properties = new Properties();
        properties.setProperty("content", "useful");

        RootNode original = Config.from(properties);
        String valueBefore = original.child("content").stringValue().obtain();

        RootNode added = original.add(getClass().getResourceAsStream("TestRootNode.properties"),
            StandardCharsets.UTF_8);

        String value = added.child("content").stringValue().obtain();
        assertThat(value).isEqualTo("nonsense");

        String valueAfter = original.child("content").stringValue().obtain();
        assertThat(valueAfter).isEqualTo(valueBefore);
    }
}
