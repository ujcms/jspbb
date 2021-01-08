package com.jspbb.core.domain

import com.jspbb.util.web.Dates
import java.time.OffsetDateTime

/**
 * 用户限制实体类。用于登录错误次数控制、发表内容次数控制、上传量控制。
 */
open class UserRestrict(
        /** 用户ID */
        var userId: Long = Long.MIN_VALUE,
        /** 限制名称 */
        var name: String = "",
        /** 计数 */
        var count: Long = 0,
        /** 计数开始时间 */
        var start: OffsetDateTime = Dates.EPOCH,
        /** 最后一次时间 */
        var last: OffsetDateTime = Dates.EPOCH
) {
    fun startWithinMinutes(minutes: Int) = start.plusMinutes(minutes.toLong()) > OffsetDateTime.now()
    fun startOutOfMinutes(minutes: Int, max: Int, current: Long = 1) = count + current <= max || start.plusMinutes(minutes.toLong()) < OffsetDateTime.now()

    fun lastOutOfSeconds(seconds: Int) = last.plusSeconds(seconds.toLong()) < OffsetDateTime.now()
}