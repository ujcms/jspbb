package com.jspbb.core.domain

import com.jspbb.util.web.Dates
import java.time.OffsetDateTime

/**
 * IP限制实体类。用于登录发送短信控制。
 */
data class IpRestrict(
        /** 用户ID */
        var ip: String = "",
        /** 限制名称 */
        var name: String = "",
        /** 计数 */
        var count: Long = 0,
        /** 计数开始时间 */
        var start: OffsetDateTime = Dates.EPOCH,
        /** 最后一次时间 */
        var last: OffsetDateTime = Dates.EPOCH
)