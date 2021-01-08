package com.jspbb.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.OffsetDateTime

/**
 * 通知实体类
 */
open class Notification(
        var id: Long = Long.MIN_VALUE,
        /** 用户ID */
        var userId: Long = Long.MIN_VALUE,
        /** 类型 */
        var type: String = "",
        /** 数据。不能为空，如无数据则默认为0 */
        var data: String = "0",
        /** 创建日期 */
        var created: OffsetDateTime = OffsetDateTime.now(),
        /** 内容 */
        var body: String = "",
        /** 内容 */
        var url: String? = null,
        /** 接收者 */
        @JsonIgnore open var user: User = User()
) {
    fun getTag() = "$type-$data"

    companion object {
        /** 通知表名 */
        const val TABLE_NAME = "jspbb_notification"
    }
}