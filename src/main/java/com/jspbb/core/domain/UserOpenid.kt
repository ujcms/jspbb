package com.jspbb.core.domain

/**
 * 用户OpenID实体类
 */
data class UserOpenid(
        /** 用户ID */
        var userId: Long = Long.MIN_VALUE,
        /** 提供商，如qq、weibo、weixin */
        var provider: String = "",
        /** OpenID */
        var openid: String = "",
        /** 微信同一用户在不同应用中的OpenID不一样，另外提供统一用户ID */
        var unionid: String? = null,
        /** 显示名称 */
        var displayName: String = "",
        /** 性别(m:男;f:女;n:保密) */
        var gender: String = "n",
        /** 头像URL */
        var pictureUrl: String? = null,
        /** 大头像URL */
        var largePictureUrl: String? = null
)