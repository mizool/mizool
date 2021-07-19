package com.github.mizool.core.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;

import java.util.Properties;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.mizool.core.exception.ConfigurationException;

public class TestValue
{
    private static final String KEY = "some.property";

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

        Throwable throwable = catchThrowable(value::read);

        assertThat(throwable).isExactlyInstanceOf(ConfigurationException.class)
            .hasMessageContaining(KEY)
            .hasCauseExactlyInstanceOf(NumberFormatException.class);
    }

    @Test
    public void testConversionSkippedForMissingKey()
    {
        Value<String> value = new Value<>(new Properties(), KEY, s -> fail("conversion was called for missing key"));

        // Ignore the result of 'read' as that is covered elsewhere
        value.read();
    }

    @Test(dataProvider = "absentValues")
    public void testAbsentValues(String remark, Properties properties)
    {
        Value<String> value = new Value<>(properties, KEY, s -> s);
        assertThat(value.read()).isNotPresent();
    }

    @DataProvider
    public Object[][] absentValues()
    {
        return new Object[][]{
            new Object[]{ "unset", new Properties() },
            new Object[]{ "set to empty string", singletonProperty("") }
        };
    }

    @Test(dataProvider = "presentValues")
    public void testPresentValues(String remark, String rawPropertyValue)
    {
        Properties properties = singletonProperty(rawPropertyValue);

        Value<String> value = new Value<>(properties, KEY, s -> s);

        // Ensure that the value is not altered in any way (e.g. by trimming whitespace).
        assertThat(value.read()).contains(rawPropertyValue);
    }

    @DataProvider
    public Object[][] presentValues()
    {
        return new Object[][]{
            new Object[]{ "regular value", "quux" },
            new Object[]{ "one space only", " " },
            new Object[]{ "trailing space", "foo " },
            new Object[]{ "leading space", " bar" },
            new Object[]{ "leading & trailing space", " foobar " }
        };
    }

    private Properties singletonProperty(String value)
    {
        Properties properties = new Properties();
        properties.setProperty(KEY, value);
        return properties;
    }
}
