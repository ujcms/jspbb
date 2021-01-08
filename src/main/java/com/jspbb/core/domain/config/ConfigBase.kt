package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.domain.Configs

/**
 * 基础设置类
 */
data class ConfigBase(
        /** 常规主题 */
        var themeNormal: String = "default",
        /** 手机主题 */
        var themeMobile: String = "default",
        // 不为平板制作单独主题
//        /** 平板主题 */
//        var themeTablet: String = "default",
        /** 网站关键词 */
        var siteKeywords: String? = null,
        /** 网站描述 */
        var siteDescription: String? = null,
        /** 网站名称 */
        var siteName: String = "jspBB"
) {
    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        const val TYPE_PREFIX = "base_"
    }
}