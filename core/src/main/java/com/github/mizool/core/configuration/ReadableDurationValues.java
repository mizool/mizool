package com.github.mizool.core.configuration;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
class ReadableDurationValues
{
    private final Set<ChronoUnit> UNITS = EnumSet.of(ChronoUnit.NANOS,
        ChronoUnit.MICROS,
        ChronoUnit.MILLIS,
        ChronoUnit.SECONDS,
        ChronoUnit.MINUTES,
        ChronoUnit.HOURS,
        ChronoUnit.DAYS);

    private final Pattern PATTERN = Pattern.compile("(\\d+) ?(" + getDurationUnitNames(Collectors.joining("|")) + ")");

    private String getDurationUnitNames(Collector<CharSequence, ?, String> collector)
    {
        return UNITS.stream()
            .map(Enum::name)
            .map(String::toLowerCase)
            .collect(collector);
    }

    public Duration parse(String s)
    {
        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException(MessageFormat.format(
                "Could not parse ''{0}''. Must be an integer followed by 0-1 space and one of {1}.",
                s,
                getDurationUnitNames(Collectors.joining(", "))));
        }
        return Duration.of(Long.parseLong(matcher.group(1)),
            ChronoUnit.valueOf(matcher.group(2)
                .toUpperCase(Locale.ROOT)));
    }
}
