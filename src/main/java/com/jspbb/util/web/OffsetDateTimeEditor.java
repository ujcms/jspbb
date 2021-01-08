package com.jspbb.util.web;

import org.joda.time.DateTime;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * 日期编辑器。支持格式：`yyyy-MM-dd` `yyyy-MM-dd HH:mm:ss` `yyyy-MM-ddTHH:mm:ss`。
 *
 * @author liufang
 * @see DateTime
 */
public class OffsetDateTimeEditor extends PropertyEditorSupport {
    /**
     * 将日期转换成字符串
     */
    @Override
    public String getAsText() {
        OffsetDateTime offsetDateTime = (OffsetDateTime) getValue();
        if (offsetDateTime != null) return Dates.formatDateTime(offsetDateTime);
        return "";
    }

    /**
     * 将字符串转换成日期
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Dates.parseOffsetDateTime(text));
    }
}
