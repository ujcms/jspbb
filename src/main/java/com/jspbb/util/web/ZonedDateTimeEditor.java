package com.jspbb.util.web;

import org.joda.time.DateTime;

import java.beans.PropertyEditorSupport;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * 日期编辑器。支持格式：`yyyy-MM-dd` `yyyy-MM-dd HH:mm:ss` `yyyy-MM-ddTHH:mm:ss`。
 *
 * @author liufang
 * @see DateTime
 */
public class ZonedDateTimeEditor extends PropertyEditorSupport {
    /**
     * 将日期转换成字符串
     */
    @Override
    public String getAsText() {
        ZonedDateTime zonedDateTime = (ZonedDateTime) getValue();
        if (zonedDateTime != null) return Dates.formatDateTime(zonedDateTime);
        else return "";
    }

    /**
     * 将字符串转换成日期
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Dates.parseOffsetDateTime(text));
    }
}
