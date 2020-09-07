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
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.ConfigurationException;
import com.google.common.collect.ImmutableList;

public class TestConfig
{
    private static final String VALUE = "foo";

    private Properties testProperties;

    @BeforeMethod
    void setUp()
    {
        testProperties = new Properties();
        testProperties.setProperty("answer", "forty-two");
        testProperties.setProperty("something", "interesting");

        testProperties.setProperty("a", VALUE);
        testProperties.setProperty("a.b", VALUE);
        testProperties.setProperty("a.b.c", VALUE);
    }

    @Test
    public void testProperties()
    {
        RootNode root = Config.from(testProperties);

        assertThat(root.child("answer")
            .stringValue()
            .obtain()).isEqualTo("forty-two");
        assertThat(root.child("something")
            .stringValue()
            .obtain()).isEqualTo("interesting");
    }

    @Test
    public void testPropertiesWithDefaults()
    {
        Properties propertiesWithDefaults = new Properties(testProperties);
        propertiesWithDefaults.setProperty("greeting", "Hello, World!");

        RootNode root = Config.from(propertiesWithDefaults);
        assertThat(root.child("answer")
            .stringValue()
            .obtain()).isEqualTo("forty-two");
        assertThat(root.child("something")
            .stringValue()
            .obtain()).isEqualTo("interesting");
        assertThat(root.child("greeting")
            .stringValue()
            .obtain()).isEqualTo("Hello, World!");
    }

    @Test
    public void testSystemProperties()
    {
        RootNode root = Config.systemProperties();
        String lineSeparator = root.child("line.separator")
            .stringValue()
            .obtain();
        assertThat(lineSeparator).isNotEmpty();
    }

    @Test
    public void testConfigPicksUpPropertiesChangedLater()
    {
        Properties properties = new Properties();
        PropertyNode node = Config.from(properties)
            .child("breaking");

        properties.setProperty("breaking", "news");

        assertThat(node.stringValue()
            .obtain()).isEqualTo("news");
    }

    @Test
    public void testGetSuccess()
    {
        Optional<String> answer = Config.from(testProperties)
            .child("answer")
            .stringValue()
            .read();

        assertThat(answer).contains("forty-two");
    }

    @Test
    public void testGetFailure()
    {
        Optional<String> answer = Config.from(testProperties)
            .child("question")
            .stringValue()
            .read();

        assertThat(answer).isEmpty();
    }

    @Test
    public void testObtainSuccess()
    {
        String answer = Config.from(testProperties)
            .child("answer")
            .stringValue()
            .obtain();

        assertThat(answer).isEqualTo("forty-two");
    }

    @Test
    public void testObtainFailure()
    {
        Value<String> configValue = Config.from(testProperties)
            .child("question")
            .stringValue();

        Throwable throwable = catchThrowable(configValue::obtain);

        assertThat(throwable).isExactlyInstanceOf(ConfigurationException.class)
            .hasMessageContaining("question");
    }

    @Test(dataProvider = "typeConversions")
    public <T> void testTypeConversion(String remark, ConversionSpec<T> conversionSpec)
    {
        Properties properties = new Properties();
        properties.setProperty("key", conversionSpec.getString());
        PropertyNode node = Config.from(properties)
            .child("key");

        T result = conversionSpec.getFunction()
            .apply(node)
            .obtain();
        assertThat(result).isEqualTo(conversionSpec.expectedValue);
    }

    @DataProvider
    public Object[][] typeConversions()
    {
        return new Object[][]{
            new Object[]{ "String", new ConversionSpec<>("foo", PropertyNode::stringValue, "foo") },
            new Object[]{ "Integer", new ConversionSpec<>("17", PropertyNode::intValue, 17) },
            new Object[]{
                "Long", new ConversionSpec<>(String.valueOf(Long.MAX_VALUE), PropertyNode::longValue, Long.MAX_VALUE)
            },
            new Object[]{
                "BigDecimal", new ConversionSpec<>("0.1", PropertyNode::bigDecimalValue, new BigDecimal("0.1"))
            },
            new Object[]{ "Boolean", new ConversionSpec<>("false", PropertyNode::booleanValue, false) },
            new Object[]{
                "Path",
                new ConversionSpec<>("local/bin",
                    node -> node.pathValue(Paths.get("usr")),
                    Paths.get("usr", "local", "bin"))
            },
            new Object[]{
                "Absolute URL",
                new ConversionSpec<>("https://example.org", PropertyNode::urlValue, createUrl("https://example.org"))
            },
            new Object[]{
                "Relative URL",
                new ConversionSpec<>("some/path",
                    node -> node.urlValue(createUrl("https://example.org")),
                    createUrl("https://example.org/some/path"))
            },
            new Object[]{
                "Unix Timestamp",
                new ConversionSpec<>("1597409235",
                    PropertyNode::unixTimestampValue,
                    OffsetDateTime.of(2020, 8, 14, 12, 47, 15, 0, ZoneOffset.UTC)
                        .toInstant())
            },
            new Object[]{
                "UTC Instant",
                new ConversionSpec<>("2020-08-14T12:47:15.00Z",
                    PropertyNode::utcInstantValue,
                    OffsetDateTime.of(2020, 8, 14, 12, 47, 15, 0, ZoneOffset.UTC)
                        .toInstant())
            },
            new Object[]{
                "ISO Duration", new ConversionSpec<>("PT7H", PropertyNode::isoDurationValue, Duration.ofHours(7))
            },
            new Object[]{
                "ISO Period", new ConversionSpec<>("P5Y12M7D", PropertyNode::isoPeriodValue, Period.of(5, 12, 7))
            },
            new Object[]{
                "Readable Duration",
                new ConversionSpec<>("30 seconds", PropertyNode::readableDuration, Duration.ofSeconds(30))
            },
            new Object[]{
                "Zone ID", new ConversionSpec<>("Europe/Berlin", PropertyNode::zoneIdValue, ZoneId.of("Europe/Berlin"))
            }
        };
    }

    public URL createUrl(String s)
    {
        return UrlValues.parse(s);
    }

    @Test(dataProvider = "stringLists")
    public void testStringLists(String remark, String value, List<String> expectedResult)
    {
        Properties properties = new Properties();
        properties.setProperty("key", value);

        List<String> result = Config.from(properties)
            .child("key")
            .stringsValue()
            .obtain()
            .collect(Collectors.toList());

        assertThat(result).isEqualTo(expectedResult);
    }

    @DataProvider
    protected Object[][] stringLists()
    {
        return new Object[][]{
            new Object[]{ "basic", "foo,bar,quux", ImmutableList.of("foo", "bar", "quux") },
            new Object[]{ "trims spaces", "foo, bar, quux", ImmutableList.of("foo", "bar", "quux") },
            new Object[]{ "trailing comma ignored", "foo,bar,quux,", ImmutableList.of("foo", "bar", "quux") },
            new Object[]{ "leading comma ignored", ",foo,bar,quux", ImmutableList.of("foo", "bar", "quux") },
            new Object[]{ "empty element skipped", "foo,bar,,quux", ImmutableList.of("foo", "bar", "quux") },
            new Object[]{
                "whitespace-only element skipped", "foo,bar,   ,quux", ImmutableList.of("foo", "bar", "quux")
            },
            new Object[]{ "singleton", "baz", ImmutableList.of("baz") },
            };
    }

    @lombok.Value
    @RequiredArgsConstructor
    private static class ConversionSpec<T>
    {
        String string;

        @ToString.Exclude
        Function<PropertyNode, Value<T>> function;
        T expectedValue;
    }

    @Test(dataProvider = "plainSuccess")
    public void testPlainObtainSuccess(String remark, HasChildren subject, String key)
    {
        assertThat(subject.child(key)
            .stringValue()
            .obtain()).isEqualTo(VALUE);
    }

    @Test(dataProvider = "plainSuccess")
    public void testPlainGetSuccess(String remark, HasChildren subject, String key)
    {
        assertThat(subject.child(key)
            .stringValue()
            .read()
            .orElseThrow(AssertionError::new)).isEqualTo(VALUE);
    }

    @Test(dataProvider = "plainFailure", expectedExceptions = ConfigurationException.class)
    public void testPlainObtainFailure(String remark, HasChildren subject, String key)
    {
        subject.child(key)
            .stringValue()
            .obtain();
    }

    @Test(dataProvider = "plainFailure")
    public void testPlainGetFailure(String remark, HasChildren subject, String key)
    {
        assertThat(subject.child(key)
            .stringValue()
            .read()).isEmpty();
    }

    @DataProvider
    protected Object[][] plainSuccess()
    {
        return new Object[][]{
            new Object[]{ "root -> 'a'", Config.from(testProperties), "a" },
            new Object[]{ "root -> 'a.b'", Config.from(testProperties), "a.b" },
            new Object[]{ "root -> 'a.b.c'", Config.from(testProperties), "a.b.c" },
            new Object[]{ "root -> 'a' -> 'b'", Config.from(testProperties).child("a"), "b" },
            new Object[]{ "root -> 'a' -> 'b.c'", Config.from(testProperties).child("a"), "b.c" },
            new Object[]{ "root -> 'a.b' -> 'c'", Config.from(testProperties).child("a.b"), "c" },
            new Object[]{
                "root -> 'a' -> 'b' -> 'c'",
                Config.from(testProperties)
                    .child("a").child("b"),
                "c"
            }
        };
    }

    @DataProvider
    protected Object[][] plainFailure()
    {
        return new Object[][]{
            new Object[]{ "root -> 'x'", Config.from(testProperties), "x" },
            new Object[]{ "root -> 'a.x'", Config.from(testProperties), "a.x" },
            new Object[]{ "root -> 'a.b.x'", Config.from(testProperties), "a.b.x" },
            new Object[]{ "root -> 'a' -> 'x'", Config.from(testProperties).child("a"), "x" },
            new Object[]{ "root -> 'a' -> 'b.x'", Config.from(testProperties).child("a"), "b.x" },
            new Object[]{ "root -> 'a.b' -> x", Config.from(testProperties).child("a.b"), "x" },
            new Object[]{
                "root -> 'a' -> 'b' -> 'x'",
                Config.from(testProperties)
                    .child("a").child("b"),
                "x"
            }
        };
    }

    @Test
    public void testAbsoluteNodeReferences()
    {
        RootNode rootNode = readNodeReferencesTestData();

        PropertyNode agencyListNode = rootNode.child("agencies");
        List<String> agencyParents = retrieveValuesViaReferencedParentNodes(agencyListNode, "parent");
        assertThat(agencyParents).containsExactly("federation", "cardassia", "romulus");
    }

    @Test
    public void testRelativeNodeReferences()
    {
        RootNode rootNode = readNodeReferencesTestData();

        PropertyNode shipModeListNode = rootNode.child("ship.modes");
        List<String> shipModeLabels = retrieveValuesViaReferencedParentNodes(shipModeListNode, "label");
        assertThat(shipModeLabels).containsExactly("Regular ship operations", "(Classified)");
    }

    public List<String> retrieveValuesViaReferencedParentNodes(PropertyNode listNode, String childName)
    {
        return listNode.referencedNodes()
            .obtain()
            .map(propertyNode -> propertyNode.child(childName)
                .stringValue()
                .obtain())
            .collect(Collectors.toList());
    }

    public RootNode readNodeReferencesTestData()
    {
        return Config.blank()
            .add(getClass().getResourceAsStream("TestConfigNodeReferences.properties"), StandardCharsets.UTF_8);
    }
}
