package com.jspbb.core.domain

/**
 * 附件引用实体类
 */
class AttachRef(
        /** 附件ID */
        var attachId: Long = Long.MIN_VALUE,
        /** 引用对象类型 */
        var refType: String = "",
        /** 引用对象ID */
        var refId: Long = Long.MIN_VALUE
) {

}