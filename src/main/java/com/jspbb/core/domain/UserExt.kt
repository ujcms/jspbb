package com.jspbb.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.Constants
import com.jspbb.core.Constants.IP_NONE
import org.apache.commons.io.FilenameUtils
import java.time.OffsetDateTime

//@JsonIgnoreProperties(value = ["configs"])
open class UserExt(
        var id: Long = Long.MIN_VALUE,
        /** 未加密密码 */
        var rowPassword: String? = null,
        /** 密码。为0代表没有设置密码，任何密码都无法登录成功（因为 hash 加密至少为32位）。当使用 oauth 注册时，就会出现这种情况。 */
        @JsonIgnore
        var password: String = "0",
        /** 密码盐。默认为0，不使用空串，避免oracle将空串自动转换成null导致错误。 */
        @JsonIgnore
        var salt: String = "0",
        /** 性别(m:男;f:女;n:保密) */
        var gender: String = "n",
        /** 头像URL */
        var pictureUrl: String? = null,
        /** 头像版本号。0代表未上传头像 */
        var pictureVersion: Int = 0,
        /** 个性签名 */
        var title: String? = null,
        /** 出生日期 */
        var birthday: OffsetDateTime? = null,
        /** 居住地 */
        var location: String? = null,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null,
        /** 登录时间 */
        var loginDate: OffsetDateTime = OffsetDateTime.now(),
        /** 登录IP */
        var loginIp: String = IP_NONE,
        /** 登录国家 */
        var loginIpCountry: String? = null,
        /** 登录省份 */
        var loginIpProvince: String? = null,
        /** 登录城市 */
        var loginIpCity: String? = null,
        /** 登录次数 */
        var loginCount: Int = 0,
        /** 上传长度 */
        var uploadedLength: Long = 0
) {
    /**
     * 密码为默认值 "0" 代表没有密码。使用oauth注册时会出现这种情况。
     */
    @JsonIgnore
    fun isHasPassword() = password != "0"

    fun loginSuccess(ip: String, country: String?, province: String?, city: String?) {
        loginIp = ip
        loginCount += 1
        loginIpCountry = country
        loginIpProvince = province
        loginIpCity = city
        loginDate = OffsetDateTime.now()
    }

    /**
     * 获取头像文件名。格式如 /avatar/12-v1.png
     */
    fun getPictureFilename(extension: String): String = "/avatar/$id-v$pictureVersion.$extension"

    /**
     * 小图。60px。
     */
    fun getPictureUrlSmall(): String = pictureUrl ?: Constants.USER_PICTURE_URL

    /**
     * 中图。240px。
     */
    fun getPictureUrlMedium(): String {
        val url = pictureUrl ?: Constants.USER_PICTURE_URL
        return getPictureUrlMedium(url)
    }

    /**
     * 大图。960px。
     */
    fun getPictureUrlLarge(): String {
        val url = pictureUrl ?: Constants.USER_PICTURE_URL
        return getPictureUrlLarge(url)
    }

    /**
     * 原图。
     */
    fun getPictureUrlOriginal(): String {
        val url = pictureUrl ?: Constants.USER_PICTURE_URL
        return getPictureUrlOriginal(url)
    }

    companion object {
        fun getPictureUrlMedium(url: String): String = getPictureUrl(url, "medium")
        fun getPictureUrlLarge(url: String): String = getPictureUrl(url, "large")
        fun getPictureUrlOriginal(url: String): String = getPictureUrl(url, "original")
        fun getPictureUrl(url: String, type: String): String {
            val fullPath = FilenameUtils.getFullPath(url)
            val baseName = FilenameUtils.getBaseName(url)
            val extension = FilenameUtils.getExtension(url)
            return "$fullPath${baseName}_$type.$extension"
        }

        /** 密码盐字节长度 */
        const val SALT_BYTE_LENGTH = 16
    }
}