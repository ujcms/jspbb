package com.jspbb.core

/**
 * 常量类
 */
object Constants {
    /** 无IP */
    const val IP_NONE = "none"

    /** 返回URL参数名 */
    const val FALLBACK_URL_PARAM = "fallbackUrl";

    /** 后台管理路径 */
    const val CP = "/cp"

    /** 标识独立用户的Cookie名 */
    const val IDENTITY_COOKIE_NAME = "_jspbb"

    /** 上传路径 */
    const val UPLOADS = "/uploads"

    /** 用户默认头像 */
    const val USER_PICTURE_URL = "/img/profile_picture.png"

    /** 编辑页面标识 */
    const val EDITING = "editing"

    /** 前台 RESTful URL 地址前缀 */
    const val API = "/api"

    /** 后台 RESTful URL 地址前缀 */
    const val APICP = "/api/cp"

    /** 默认每页条数 */
    const val DEFAULT_PAGE_SIZE = 10

    /** 默认每页最大条数 */
    const val MAX_PAGE_SIZE = 2000

    fun validPageSize(pageSize: Int?, default: Int = DEFAULT_PAGE_SIZE): Int = when {
        pageSize == null || pageSize < 1 -> default
        pageSize > MAX_PAGE_SIZE -> MAX_PAGE_SIZE
        else -> pageSize
    }

    fun validPage(page: Int?): Int = if (page == null || page < 1) 1 else page
}
