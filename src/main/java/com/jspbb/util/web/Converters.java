package com.jspbb.util.web;

import org.springframework.lang.Nullable;

// "Invalid boolean value [" + text + "]"
public abstract class Converters {
    @Nullable
    public static Integer getInteger(@Nullable Object param) {
        if (param == null) return null;
        else if (param instanceof Integer) return (Integer) param;
        else if (param instanceof Number) return ((Number) param).intValue();
        else if (param instanceof String) return Integer.parseInt((String) param);
        else throw new IllegalArgumentException("Could not parse Integer:" + param);
    }

    @Nullable
    public static Long getLong(@Nullable Object param) {
        if (param == null) return null;
        else if (param instanceof Long) return (Long) param;
        else if (param instanceof Number) return ((Number) param).longValue();
        else if (param instanceof String) return Long.parseLong((String) param);
        else throw new IllegalArgumentException("Could not parse Long:" + param);
    }
}
