package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import java.time.OffsetDateTime

/**
 * 附件实体类
 */
data class Attach(
        var id: Long = Long.MIN_VALUE,
        /** 用户ID */
        var userId: Long? = null,
        /** 完整名称，含路径 */
        var name: String = "",
        /** URL */
        var url: String = "",
        /** 原始文件名 */
        var origName: String? = null,
        /** 附件长度 */
        var length: Long = 0,
        /** 上传日期 */
        var date: OffsetDateTime = OffsetDateTime.now(),
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null,
        /** 是否使用 */
        var used: Boolean = false
) {
    companion object {
        /** 附件表名 */
        const val TABLE_NAME = "jspbb_attach"
    }
}