package com.jspbb.util.web;

import org.joda.time.DateTime;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * 日期编辑器。支持格式：`yyyy-MM-dd`。
 *
 * @author liufang
 * @see DateTime
 */
public class LocalDateEditor extends PropertyEditorSupport {
    /**
     * 将日期转换成字符串
     */
    @Override
    public String getAsText() {
        LocalDate localDate = (LocalDate) getValue();
        if (localDate != null) return localDate.toString();
        else return "";
    }

    /**
     * 将字符串转换成日期
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(Dates.parseLocalDate(text));
    }
}
