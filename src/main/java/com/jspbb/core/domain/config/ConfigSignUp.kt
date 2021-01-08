package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.jspbb.core.Constants
import com.jspbb.core.domain.Configs

/**
 * 注册配置类
 */
data class ConfigSignUp(
//        @JsonIgnore
//        val pictureUrl: String = Constants.USER_PICTURE_URL,
        /** 小头像尺寸 */
        var pictureSizeSmall: Int = 60,
        /** 中头像尺寸 */
        var pictureSizeMedium: Int = 240,
        /** 大头像尺寸 */
        var pictureSizeLarge: Int = 960,
        /** 用户名最小长度 */
        var usernameMinLength: Int = 4,
        /** 用户名最大长度 */
        var usernameMaxLength: Int = 12,
        /** 用户名正则表达式。默认允许为数字 字符 . - @ _ */
        var usernameRegex: String = "^[0-9a-zA-Z\\.\\-@_]+\$",
        /** 验证模式。0: 不验证; 1:图形验证码; 2:邮件或短信验证码; 3:邮件验证码; 4:短信验证码; 5:邀请码; */
        var verifyMode: Int = 1,
        /** 验证码邮件主题 */
        var verifyEmailSubject: String = "请确认您的电子邮箱",
        /** 验证码邮件正文 */
        var verifyEmailText: String = "验证码：\${code}，二十分钟内有效。"
) {
    /** 默认头像URL 固定值不可改*/
    fun getPictureUrl() = Constants.USER_PICTURE_URL

    /**
     * 获取替换了验证码的邮件正文
     */
    fun getReplacedVerifyEmailText(code: String) = verifyEmailText.replace("${'$'}{code}", code)

    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        const val TYPE_PREFIX = "signUp_"
        /** 验证模式：不验证 */
        const val VERIFY_MODE_NONE = 0
        /** 验证模式：图形验证码 */
        const val VERIFY_MODE_PICTURE = 1
        /** 验证模式：邮件或短信验证码 */
        const val VERIFY_MODE_EMAIL_OR_MOBILE = 2
        /** 验证模式：邮件验证码 */
        const val VERIFY_MODE_EMAIL = 3
        /** 验证模式：短信验证码 */
        const val VERIFY_MODE_MOBILE = 4
        /** 验证模式：邀请码 */
        const val VERIFY_MODE_INVITATION = 5
    }
}