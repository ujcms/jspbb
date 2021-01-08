package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * 访问实体类
 */
open class Access(
        var id: Long = Long.MIN_VALUE,
        /** 访问者ID */
        var userId: Long? = null,
        /** 访问日期 */
        var date: OffsetDateTime = OffsetDateTime.now(),
        /** 访问日期字符串年月日 */
        var dateYmd: String = dateFormat.format(date),
        /** 访问日期字符串年月日时 */
        var dateYmdh: String = dateHHFormat.format(date),
        /** 访问日期字符串年月日时分 */
        var dateYmdhi: String = dateHHmmFormat.format(date),
        /** IP */
        var ip: String = IP_NONE,
        /** COOKIE值 */
        var cookie: Long = 0,
        /** 来源 */
        var source: String = SOURCE_DIRECT,
        /** 浏览器 */
        var browser: String? = null,
        /** 操作系统 */
        var os: String? = null,
        /** 设备 */
        var device: Char? = null,
        /** 国家 */
        var country: String? = null,
        /** 省份 */
        var province: String? = null,
        /** 城市 */
        var city: String? = null,
        /** 宽带提供商 */
        var provider: String? = null,
        /** 用户 */
        open var user: User? = null,
        /** 访问扩展类 */
        open var ext: AccessExt = AccessExt()
) {
    companion object {
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd")
        val dateHHFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHH")
        val dateHHmmFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm")

        /** 访问表名 */
        const val TABLE_NAME = "jspbb_access"
        const val SOURCE_DIRECT = "DIRECT"

        /** 设备：未知 */
        const val DEVICE_UNKNOWN = '0'
        /** 设备：电脑 */
        const val DEVICE_COMPUTER = '1'
        /** 设备：手机 */
        const val DEVICE_MOBILE = '2'
        /** 设备：平板 */
        const val DEVICE_TABLET = '3'
        /** 设备：游戏机 */
        const val DEVICE_GAME_CONSOLE = '4'
        /** 设备：机顶盒 */
        const val DEVICE_DMR = '5'
        /** 设备：智能手表（智能眼镜） */
        const val DEVICE_WEARABLE = '6'
    }
}