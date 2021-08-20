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
