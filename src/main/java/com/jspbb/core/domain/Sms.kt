package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import java.time.OffsetDateTime

/**
 * 短信息 实体类
 */
data class Sms(
        var id: Long = Long.MIN_VALUE,
        /** 类型 */
        var type: String = TYPE_MOBILE,
        /** 用途 */
        var usage: String = USAGE_SIGN_UP,
        /** 接收者（手机号码或者邮箱地址） */
        var receiver: String = "",
        /** 验证码 */
        var code: String = "",
        /** 发送时间 */
        var sendDate: OffsetDateTime = OffsetDateTime.now(),
        /** 尝试次数(-1代表已使用) */
        var tryCount: Int = 0,
        /** 状态(0:未使用,1:验证正确,2:验证错误,3:尝试次数过多,4:过期) */
        var status: Int = 0,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null
) {
    /** 是否过期 */
    fun isExpired() = sendDate.plusMinutes(EXPIRES).isBefore(OffsetDateTime.now())

    /** 是否未使用 */
    fun isUnused() = status == STATUS_UNUSED

    companion object {
        /** 用户表名 */
        const val TABLE_NAME = "jspbb_sms"
        /** 类型：手机验证码 */
        const val TYPE_MOBILE = "mobile"
        /** 类型：邮件验证码 */
        const val TYPE_EMAIL = "email"
        /** 用途：注册 */
        const val USAGE_SIGN_UP = "signUp"
        /** 用途：修改邮箱地址 */
        const val USAGE_CHANGE_EMAIL = "changeEmail"
        /** 用途：修改手机号码 */
        const val USAGE_CHANGE_MOBILE = "changeMobile"
        /** 用途：重置密码 */
        const val USAGE_RESET_PASSWORD = "resetPassword"
        /** 状态：未使用 */
        const val STATUS_UNUSED = 0
        /** 状态：验证正确 */
        const val STATUS_CORRECT = 1
        /** 状态：验证错误 */
        const val STATUS_WRONG = 2
        /** 状态：尝试次数过多 */
        const val STATUS_TOO_MANY_ATTEMPTS = 3
        /** 状态：已过期 */
        const val STATUS_EXPIRED = 4
        /** 验证码允许尝试的最大次数 */
        const val MAX_TRY_COUNT: Int = 10
        /** 验证码有效时间。单位：分钟 */
        const val EXPIRES: Long = 20
    }
}