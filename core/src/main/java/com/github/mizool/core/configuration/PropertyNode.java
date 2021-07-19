package com.github.mizool.core.configuration;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;

import com.google.common.base.Splitter;

/**
 * An object that allows type-safe retrieval of property values and navigation to child properties.
 */
@RequiredArgsConstructor
public final class PropertyNode implements HasChildren
{
    private static final String SEPARATOR = ".";

    private final Properties properties;
    private final String key;

    /**
     * Navigates to the given child node.
     *
     * @param reference one or more segments of the key to append to the key of the current node
     *
     * @return the child or descendant node, never {@code null}.
     */
    @Override
    public PropertyNode child(String reference)
    {
        if (reference.isEmpty())
        {
            throw new IllegalArgumentException("Reference must not be blank");
        }

        return new PropertyNode(this.properties, this.key + SEPARATOR + reference);
    }

    /**
     * Gets the property value as a string.
     *
     * @return the string
     */
    public Value<String> stringValue()
    {
        return convertedValue(string -> string);
    }

    /**
     * Parses the property value as a boolean.
     *
     * @return the boolean
     */
    public Value<Boolean> booleanValue()
    {
        return convertedValue(Boolean::parseBoolean);
    }

    /**
     * Parses the property value as an integer.
     *
     * @return the integer
     */
    public Value<Integer> intValue()
    {
        return convertedValue(Integer::parseInt);
    }

    /**
     * Parses the property value as a long.
     *
     * @return the long
     */
    public Value<Long> longValue()
    {
        return convertedValue(Long::parseLong);
    }

    /**
     * Parses the property value as a big decimal.
     *
     * @return the big decimal
     */
    public Value<BigDecimal> bigDecimalValue()
    {
        return convertedValue(BigDecimal::new);
    }

    /**
     * Interprets the property value as an absolute path.
     *
     * @return the absolute path
     */
    public Value<Path> pathValue()
    {
        return convertedValue(Paths::get);
    }

    /**
     * Interprets the property value as a relative path for the given base path.
     *
     * @param basePath the path the property value is relative to
     *
     * @return the combined, absolute path
     */
    public Value<Path> pathValue(Path basePath)
    {
        return convertedValue(basePath::resolve);
    }

    /**
     * Parses the property value as an absolute URL.
     *
     * @return the URL
     */
    public Value<URL> urlValue()
    {
        return convertedValue(UrlValues::parse);
    }

    /**
     * Interprets the property value as a relative URL for the given context.
     *
     * @param context the URL the property value is relative to
     *
     * @return the combined, absolute URL
     */
    public Value<URL> urlValue(URL context)
    {
        return convertedValue(string -> UrlValues.parse(context, string));
    }

    /**
     * Parses a string containing the number of seconds from the epoch of 1970-01-01T00:00:00Z.
     *
     * @return the instant
     */
    public Value<Instant> unixTimestampValue()
    {
        return convertedValue(string -> Instant.ofEpochSecond(Long.parseLong(string)));
    }

    /**
     * Parses a string such as {@code 2020-08-14T12:04:22.00Z} using
     * {@link java.time.format.DateTimeFormatter#ISO_INSTANT}.
     *
     * @return the instant
     */
    public Value<Instant> utcInstantValue()
    {
        return convertedValue(Instant::parse);
    }

    /**
     * Parses a string such as {@code 30 seconds}.
     *
     * @return the duration
     */
    public Value<Duration> readableDuration()
    {
        return convertedValue(ReadableDurationValues::parse);
    }

    /**
     * Parses a string such as {@code PT7H} using {@link java.time.Duration#parse(CharSequence)}.
     *
     * @return the duration
     */
    public Value<Duration> isoDurationValue()
    {
        return convertedValue(Duration::parse);
    }

    /**
     * Parses a string such as {@code P5Y12M7D} using {@link java.time.Period#parse(CharSequence)}.
     *
     * @return the period
     */
    public Value<Period> isoPeriodValue()
    {
        return convertedValue(Period::parse);
    }

    /**
     * Parses a string such as {@code Europe/Berlin} using {@link java.time.ZoneId#of(String)}.
     *
     * @return the zone ID
     */
    public Value<ZoneId> zoneIdValue()
    {
        return convertedValue(ZoneId::of);
    }

    /**
     * Parses a comma-separated list of values. Empty and whitespace-only elements are skipped, and leading and trailing
     * whitespace inside each value is removed.
     *
     * @return the values
     */
    public Value<Stream<String>> stringsValue()
    {
        return convertedValue(this::parseCommaSeparatedList);
    }

    /**
     * Parses a comma-separated list of references and returns the respective property nodes.<br>
     * <br>
     * <b>Absolute and relative references</b><br>
     * To increase readability of properties files, users can reference child nodes with a shortened syntax: if a
     * reference starts with a '{@code .}', it is resolved starting with the current node (relative reference). Other
     * references are resolved starting with the root node (absolute reference).<br>
     * <br>
     * <b>Whitespace</b><br>
     * Whitespace is handled the same way as {@link #stringsValue()}.
     *
     * @return the property nodes
     */
    public Value<Stream<PropertyNode>> referencedNodes()
    {
        return convertedValue(this::lookupNodes);
    }

    private Stream<PropertyNode> lookupNodes(String references)
    {
        return parseCommaSeparatedList(references).map(reference -> {
            if (reference.startsWith(SEPARATOR))
            {
                return child(reference.substring(SEPARATOR.length()));
            }
            else
            {
                return new PropertyNode(this.properties, reference);
            }
        });
    }

    /**
     * Parses the property value using the given function.
     *
     * @return the converted value
     */
    public <T> Value<T> convertedValue(Function<String, T> valueConversionFunction)
    {
        return new Value<>(properties, key, valueConversionFunction);
    }

    private Stream<String> parseCommaSeparatedList(String string)
    {
        Iterable<String> split = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings()
            .split(string);
        return StreamSupport.stream(split.spliterator(), false);
    }

    @Override
    public String toString()
    {
        return "PropertyNode[" + key + "]";
    }
}
