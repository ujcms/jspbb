package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.domain.Configs

/**
 * 上传配置类
 *
 * input file accept 支持 .jpg,.png 格式，也支持 MIME Type。accept 属性 IE Chrome Firefox 支持，Edge 不支持。
 */
data class ConfigUpload(
        /** 允许上传的文件类型。格式如：zip,7z,gz,bz2,iso,rar,pdf,doc,docx,xls,xlsx,ppt,pptx */
        var fileTypes: String? = "zip,7z,gz,bz2,iso,rar,pdf,doc,docx,xls,xlsx,ppt,pptx",
        /** 允许上传的图片类型。格式如：jpg,jpeg,png,gif */
        var imageTypes: String? = "jpg,jpeg,png,gif",
        /** 允许上传的视频类型。格式如：mp4,m3u8 */
        var videoTypes: String? = "mp4,m3u8",
        /** 文件最大长度。单位 KB */
        var fileLimit: Int = 0,
        /** 图片最大长度。单位 KB */
        var imageLimit: Int = 0,
        /** 视频最大长度。单位 KB */
        var videoLimit: Int = 0,
        /** 图片最大宽度。等于 0 则不限制。默认1920 */
        var imageMaxWidth: Int = 1920,
        /** 图片最大高度。等于 0 则不限制。默认0 */
        var imageMaxHeight: Int = 0
) {
    fun getFileAccept(): String = getInputAccept(fileTypes)
    fun getImageAccept(): String = getInputAccept(imageTypes)
    fun getVideoAccept(): String = getInputAccept(videoTypes)
    fun getFileTypeRegex(): String = getTypeRegex(fileTypes)
    fun getImageTypeRegex(): String = getTypeRegex(imageTypes)
    fun getVideoTypeRegex(): String = getTypeRegex(videoTypes)
    fun getFileLimitByte(): Long? = if (fileLimit > 0) fileLimit * 1024L else null
    fun getImageLimitByte(): Long? = if (imageLimit > 0) imageLimit * 1024L else null
    fun getVideoLimitByte(): Long? = if (videoLimit > 0) videoLimit * 1024L else null

    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        fun getInputAccept(types: String?): String = if (!types.isNullOrBlank()) types.split(",").joinToString(",.", ".") else ""
        fun getTypeRegex(types: String?): String = if (!types.isNullOrBlank()) types.split(",").joinToString("|.", "(.", ")$") else ""
        fun isValidType(types: String?, type: String): Boolean = types == null || types.split(",").contains(type.toLowerCase())

        const val TYPE_PREFIX = "upload_"
    }
}