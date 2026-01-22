package com.github.mizool.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.testng.annotations.Test;

public class TestRootNode
{
    @SuppressWarnings("removal")
    @Test
    public void testAdd()
    {
        RootNode rootNode = Config.blank()
            .add(obtainResourceStream(), StandardCharsets.UTF_8);

        Optional<String> valueOptional = rootNode.child("content")
            .stringValue()
            .read();

        assertThat(valueOptional).contains("fromFile");
    }

    private InputStream obtainResourceStream()
    {
        return Objects.requireNonNull(getClass().getResourceAsStream("TestRootNode.properties"));
    }

    @Test
    public void testAddTakesPrecedence()
    {
        Properties properties = new Properties();
        properties.setProperty("content", "original");

        RootNode added = Config.from(properties)
            .add(obtainResourceStream(), StandardCharsets.UTF_8);

        Optional<String> valueOptional = added.child("content")
            .stringValue()
            .read();
        assertThat(valueOptional).contains("fromFile");
    }

    @Test
    public void testAddDoesNotAlterOriginal()
    {
        Properties properties = new Properties();
        properties.setProperty("content", "original");

        RootNode original = Config.from(properties);
        Optional<String> valueOptionalBefore = original.child("content")
            .stringValue()
            .read();

        RootNode added = original.add(obtainResourceStream(),
            StandardCharsets.UTF_8);

        Optional<String> valueOptional = added.child("content")
            .stringValue()
            .read();
        assertThat(valueOptional).contains("fromFile");

        Optional<String> valueOptionalAfter = original.child("content")
            .stringValue()
            .read();
        assertThat(valueOptionalAfter).isEqualTo(valueOptionalBefore);
    }
}
