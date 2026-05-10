package cn.xor7.xiaohei.cmdLog.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    );
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyyMMdd HH:mm"),
        DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
        DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    );
    private static final Pattern RELATIVE_TIME_PATTERN = Pattern.compile(
        "^([+-]?\\d+)([smhdw])$",
        Pattern.CASE_INSENSITIVE
    );
    private static final DateTimeFormatter DISPLAY_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_DAY_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TimeParser() {}

    public static LocalDateTime parseFrom(String input) {
        return parse(input, true);
    }

    public static LocalDateTime parseTo(String input) {
        return parse(input, false);
    }

    public static String formatTime(LocalDateTime time) {
        return DISPLAY_TIME_FORMATTER.format(time);
    }

    public static String formatDay(LocalDate date) {
        return DISPLAY_DAY_FORMATTER.format(date);
    }

    private static LocalDateTime parse(String input, boolean startOfDay) {
        String normalizedInput = input.trim();

        LocalDateTime relativeTime = parseRelativeTime(normalizedInput);
        if (relativeTime != null) {
            return relativeTime;
        }

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(normalizedInput, formatter);
            } catch (DateTimeParseException ignored) {}
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(normalizedInput, formatter);
                if (startOfDay) {
                    return date.atStartOfDay();
                }
                return date.atTime(23, 59, 59);
            } catch (DateTimeParseException ignored) {}
        }

        throw new DateTimeParseException(
            "Unsupported time format",
            normalizedInput,
            0
        );
    }

    private static LocalDateTime parseRelativeTime(String input) {
        Matcher matcher = RELATIVE_TIME_PATTERN.matcher(input);
        if (!matcher.matches()) {
            return null;
        }

        long amount = Long.parseLong(matcher.group(1));
        char unit = Character.toLowerCase(matcher.group(2).charAt(0));
        LocalDateTime now = LocalDateTime.now();

        return switch (unit) {
            case 's' -> now.plusSeconds(amount);
            case 'm' -> now.plusMinutes(amount);
            case 'h' -> now.plusHours(amount);
            case 'd' -> now.plusDays(amount);
            case 'w' -> now.plusWeeks(amount);
            default -> null;
        };
    }
}
