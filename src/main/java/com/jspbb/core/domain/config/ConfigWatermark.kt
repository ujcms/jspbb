package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.domain.Configs

/**
 * 水印配置类
 */
data class ConfigWatermark(
        /** 是否开启水印 */
        var enabled: Boolean = false,
        /** 水印图片地址 */
        var overlay: String = "/img/watermark.png",
        /** 水印位置。1-9。NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast。默认5，中央位置 */
        var position: Int = 5,
        /** 透明度。0-100。0: 完全透明; 100: 完全不透明。默认50 */
        var dissolve: Int = 50,
        /** 图片最小宽度。小于这个宽度的图片不加水印。默认300 */
        var minWidth: Int = 300,
        /** 图片最小高度。小于这个高度的图片不加水印。默认300 */
        var minHeight: Int = 300
) {
    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        const val TYPE_PREFIX = "watermark_"
    }
}