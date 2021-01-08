package com.jspbb.util.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Entities {
    public static void copy(Map<String, Object> fields, Object bean, Collection<String> includes, Collection<String> excludes) {
        if (fields == null || bean == null) return;
        Map<String, Object> validFields = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            // 只拷贝基础类型，List 和 Map 里都可以进一步包含对象，可以修改 Entity 中其它对象，在 JPA 中可能导致数据库数据被修改，造成安全漏洞。
            if ((includes == null || includes.isEmpty() || includes.contains(name))
                    && (excludes == null || excludes.isEmpty() || !excludes.contains(name))
                    && !(value instanceof Collection || value instanceof Map)) {
                // 将空串转为 null。因为 oracle 的 varchar2 会自动把空串转为 null，在此统一各数据库行为。
                if (value instanceof String && "".equals(value)) value = null;
                validFields.put(name, value);
            }
        }
        if (!validFields.isEmpty()) {
            try {
                // 增加 JAVA8 日期 的处理、忽略未识别的属性。
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                String json = mapper.writeValueAsString(validFields);
                mapper.readerForUpdating(bean).readValue(json);
            } catch (IOException e) {
                throw new RuntimeException("copy fields to entity properties error.", e);
            }
        }
    }

    public static void copy(Map<String, Object> fields, Object bean) {
        copy(fields, bean, null, null);
    }

    public static void copy(Map<String, Object> fields, Object bean, String... excludes) {
        copy(fields, bean, null, Arrays.asList(excludes));
    }

    public static void copyIncludes(Map<String, Object> fields, Object bean, String... includes) {
        copy(fields, bean, Arrays.asList(includes), null);
    }
}
