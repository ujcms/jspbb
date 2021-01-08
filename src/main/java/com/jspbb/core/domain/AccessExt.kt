package com.jspbb.core.domain

/**
 * 访问扩展实体类
 */
open class AccessExt(
        var id: Long = Long.MIN_VALUE,
        /** 访问URL */
        var url: String = "",
        /** 来源URL */
        var referrer: String? = null,
        /** 用户代理 */
        var userAgent: String? = null
)