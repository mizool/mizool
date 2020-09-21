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
import java.util.Optional;
import java.util.Properties;

import org.testng.annotations.Test;

public class TestRootNode
{
    @Test
    public void testAdd()
    {
        RootNode rootNode = Config.blank()
            .add(getClass().getResourceAsStream("TestRootNode.properties"), StandardCharsets.UTF_8);

        Optional<String> valueOptional = rootNode.child("content")
            .stringValue()
            .read();

        assertThat(valueOptional).contains("nonsense");
    }

    @Test
    public void testAddDoesNotAlterOriginal()
    {
        Properties properties = new Properties();
        properties.setProperty("content", "useful");

        RootNode original = Config.from(properties);
        Optional<String> valueOptionalBefore = original.child("content")
            .stringValue()
            .read();

        RootNode added = original.add(getClass().getResourceAsStream("TestRootNode.properties"),
            StandardCharsets.UTF_8);

        Optional<String> valueOptional = added.child("content")
            .stringValue()
            .read();
        assertThat(valueOptional).contains("nonsense");

        Optional<String> valueOptionalAfter = original.child("content")
            .stringValue()
            .read();
        assertThat(valueOptionalAfter).isEqualTo(valueOptionalBefore);
    }
}
