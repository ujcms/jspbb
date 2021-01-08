package com.jspbb.util.web;

import org.joda.time.DateTime;

import java.beans.PropertyEditorSupport;
import java.time.Instant;

/**
 * 日期编辑器。支持格式：`yyyy-MM-dd` `yyyy-MM-dd HH:mm:ss` `yyyy-MM-ddTHH:mm:ss` `yyyy-MM-ddTHH:mm:ssZ` `yyyy-MM-ddTHH:mm:ss.SSS` `yyyy-MM-ddTHH:mm:ss.SSSZ`。
 *
 * @author liufang
 * @see DateTime
 */
public class InstantEditor extends PropertyEditorSupport {
    /**
     * 将日期转换成字符串
     */
    @Override
    public String getAsText() {
        Instant instant = (Instant) getValue();
        if (instant != null) return Dates.formatDateTime(instant);
        else return "";
    }

    /**
     * 将字符串转换成日期
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Dates.parseInstant(text));
    }
}
