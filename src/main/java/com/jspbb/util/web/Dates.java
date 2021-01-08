package com.jspbb.util.web;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQuery;

import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

public class Dates {
    /**
     * 时间纪元 `1970-01-01T00:00:00Z`
     */
    public static OffsetDateTime EPOCH = OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());

    private static DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @NotNull
    public static LocalDateTime parseLocalDateTime(@NotNull String text) {
        return LocalDateTime.ofInstant(parseInstant(text, Instant::from), ZoneId.systemDefault());
    }

    @NotNull
    public static Instant parseInstant(@NotNull String text) {
        return parseInstant(text, Instant::from);
    }

    @NotNull
    public static OffsetDateTime parseOffsetDateTime(@NotNull String text) {
        return parseInstant(text, OffsetDateTime::from);
    }

    @NotNull
    public static ZonedDateTime parseZonedDateTime(@NotNull String text) {
        return parseInstant(text, ZonedDateTime::from);
    }

    @NotNull
    public static LocalDate parseLocalDate(@NotNull String text) {
        return LocalDate.parse(text);
    }

    @NotNull
    private static <T> T parseInstant(@NotNull String text, @NotNull TemporalQuery<T> query) {
        int length = text.length();
        if (length > 19) {
            return ISO_ZONED_DATE_TIME.parse(text, query);
        } else if (length == 19) {
            if (text.contains("T")) {
                return LocalDateTime.parse(text).atZone(ZoneId.systemDefault()).query(query);
            } else {
                return LocalDateTime.parse(text, DATETIME_FORMAT).atZone(ZoneId.systemDefault()).query(query);
            }
        } else if (length == 10) {
            return LocalDate.parse(text).atStartOfDay().atZone(ZoneId.systemDefault()).query(query);
        } else {
            throw new DateTimeParseException("Text '" + text + "' could not be parsed", text, 0);
        }
    }

    @NotNull
    public static String formatDate(@NotNull OffsetDateTime offsetDateTime) {
        return LocalDateTime.ofInstant(offsetDateTime.toInstant(), ZoneId.systemDefault()).format(DATE_FORMAT);
    }

    @NotNull
    public static String formatDateTime(@NotNull OffsetDateTime offsetDateTime) {
        return LocalDateTime.ofInstant(offsetDateTime.toInstant(), ZoneId.systemDefault()).format(DATETIME_FORMAT);
    }

    @NotNull
    public static String formatDate(@NotNull ZonedDateTime zonedDateTime) {
        return LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault()).format(DATE_FORMAT);
    }

    @NotNull
    public static String formatDateTime(@NotNull ZonedDateTime zonedDateTime) {
        return LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.systemDefault()).format(DATETIME_FORMAT);
    }

    @NotNull
    public static String formatDate(@NotNull LocalDateTime localDateTime) {
        return localDateTime.format(DATE_FORMAT);
    }

    @NotNull
    public static String formatDateTime(@NotNull LocalDateTime localDateTime) {
        return localDateTime.format(DATETIME_FORMAT);
    }

    @NotNull
    public static String formatDate(@NotNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DATE_FORMAT);
    }

    @NotNull
    public static String formatDateTime(@NotNull Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DATETIME_FORMAT);
    }

    @NotNull
    public static String formatDate(@NotNull LocalDate localDate) {
        return localDate.toString();
    }
}
