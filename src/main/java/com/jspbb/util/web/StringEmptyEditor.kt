package com.jspbb.util.web

import java.beans.PropertyEditorSupport

/**
 * 将空串转换成 null。
 * <p>
 * 由于 oracle 的 varchar2 类型不能保存空串，会自动把空串转换成 null，为了保持各数据库的一致性，把保存到数据库的空串都预先转换成 null。
 *
 * @author liufang
 */
class StringEmptyEditor : PropertyEditorSupport() {
    override fun setAsText(text: String?) {
        value = if (text == "") null else text
    }
}
