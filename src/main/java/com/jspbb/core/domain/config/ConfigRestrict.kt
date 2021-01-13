package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.domain.Configs
import org.apache.commons.lang3.StringUtils
import java.time.OffsetDateTime

data class ConfigRestrict(
        /** 密码尝试允许次数 */
        var passwordRetryMax: Int = 3,
        /** 密码尝试限制间隔（分钟） */
        var passwordRetryWithin: Int = 20,
        /** 短信发送最大条数 */
        var smsMax: Int = 5,
        /** 短信发送限制间隔（分钟） */
        var smsWithin: Int = 1440,
        /** 提交内容最小间隔时间（秒钟） */
        var postInterval: Int = 2,
        /** 不受信用户禁止发帖时间 */
        var forbiddenTime: String? = null,
        /** IP黑名单 */
        var ipBlacklist: String? = null,
        /** 手机号段黑名单 */
        var mobileBlacklist: String? = null
) {
    fun inIpBlacklist(ip: String): Boolean = if (ipBlacklist != null) StringUtils.split(ipBlacklist, ',').contains(ip) else false

    fun inMobileBlacklist(mobile: String): Boolean = if (mobileBlacklist != null) StringUtils.split(mobileBlacklist, ',').any { mobile.startsWith(it) } else false

    /**
     * 是否在禁止时间内
     */
    fun inForbiddenTime() = forbiddenTime?.split(',')?.contains(OffsetDateTime.now().hour.toString()) ?: false

    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        const val TYPE_PREFIX = "restrict_"
    }
}