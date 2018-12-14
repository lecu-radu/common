package ro.rasel.time;

import ro.rasel.collections.MapBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TimeFormatterImpl implements TimeFormatter {
    public static final TimeFormatter SIMPLE_FORMATTER = new TimeFormatterImpl(
            MapBuilder.<TimeUnit, LongFunction<String>>ofMap()
                    .put(DAYS, value -> simpleFormat(value, "d"))
                    .put(HOURS, value -> simpleFormat(value, "h"))
                    .put(MINUTES, value -> simpleFormat(value, "m"))
                    .put(SECONDS, value -> simpleFormat(value, "s"))
                    .put(MILLISECONDS, value -> simpleFormat(value, "ms"))
                    .put(NANOSECONDS, value -> simpleFormat(value, "ns"))
                    .build(HashMap::new),
            ":");
    public static final TimeFormatter VERBOSE_FORMATTER = new TimeFormatterImpl(
            MapBuilder.<TimeUnit, LongFunction<String>>ofMap()
                    .put(DAYS, value -> pluralFormat(value, "day", "days"))
                    .put(HOURS, value -> pluralFormat(value, "hour", "hours"))
                    .put(MINUTES, value -> pluralFormat(value, "minute", "minutes"))
                    .put(SECONDS, value -> pluralFormat(value, "second", "seconds"))
                    .put(MILLISECONDS, value -> pluralFormat(value, "millisecond", "milliseconds"))
                    .put(NANOSECONDS, value -> pluralFormat(value, "nanosecond", "nanoseconds"))
                    .build(HashMap::new),
            ":");

    private final String separator;
    private final Map<TimeUnit, LongFunction<String>> formatterMap;

    private TimeFormatterImpl(Map<TimeUnit, LongFunction<String>> formatterMap, String separator) {
        Stream.of(DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS, NANOSECONDS).forEach(formatterUnit -> Objects
                .requireNonNull(formatterMap.get(formatterUnit),
                        formatterUnit.name().toLowerCase() + " Formatter should not be null"));
        Objects.requireNonNull(separator, "separator should not be null");

        this.formatterMap = Collections.unmodifiableMap(new HashMap<>(formatterMap));
        this.separator = separator;
    }

    public static String simpleFormat(long value, String suffix) {
        return pluralFormat(value, suffix, suffix);
    }

    public static String pluralFormat(long value, String suffix, String pluralSuffix) {
        return value + (value == 1 ? suffix : pluralSuffix);
    }

    @Override
    public LongFunction<String> getFormatter(TimeUnit formatterUnit) {
        return formatterMap.get(formatterUnit);
    }

    @Override
    public String getSeparator() {
        return separator;
    }
}
