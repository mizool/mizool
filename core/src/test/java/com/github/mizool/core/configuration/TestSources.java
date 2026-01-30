package com.github.mizool.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import org.testng.annotations.Test;

public class TestSources
{
    @Test
    public void testJoinPrimaryTakesPrecedence() throws IOException
    {
        Properties properties = new Properties();
        properties.setProperty("content", "original");

        try (InputStream inputStream = obtainResourceStream())
        {
            Source inputStreamSource = Sources.from(inputStream, StandardCharsets.UTF_8);
            Source propertiesSource = Sources.from(properties);
            var joinedSource = Sources.join(inputStreamSource, propertiesSource);

            String actual = joinedSource.getRawValue("content");
            assertThat(actual).isEqualTo("fromFile");
        }
    }

    private InputStream obtainResourceStream()
    {
        return Objects.requireNonNull(getClass().getResourceAsStream("TestRootNode.properties"));
    }
}
