package com.jspbb.util.security.oauth

import com.github.scribejava.core.model.OAuth2AccessToken
import org.pac4j.core.profile.converter.Converters
import org.pac4j.oauth.config.OAuth20Configuration
import org.pac4j.oauth.profile.JsonHelper
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition

/**
 * 定义获取用户信息的URL地址，以及解析微信Profile的方式。
 *
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316518&lang=zh_CN">微信获取用户级别信息</a>
 */
class WeixinProfileDefinition : OAuth20ProfileDefinition<WeixinProfile>({ WeixinProfile() }) {
    init {
        listOf<String>(NICKNAME, COUNTRY, PROVINCE, CITY, UNIONID).forEach {
            primary(it, Converters.STRING)
        }
        //微信是 1:男,2:女，转换器是 1:女,2:男，要自定义转换器
        //primary(SEX, Converters.GENDER)
        primary(HEADIMGURL, Converters.URL)
        primary(LANGUAGE, Converters.LOCALE)
    }

    override fun getProfileUrl(accessToken: OAuth2AccessToken, configuration: OAuth20Configuration?): String {
        return "https://api.weixin.qq.com/sns/userinfo"
    }

    override fun extractUserProfile(body: String?): WeixinProfile {
        val profile = newProfile()
        val json = JsonHelper.getFirstNode(body)
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, OPENID))
            for (attribute in primaryAttributes) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute))
            }
        }
        return profile
    }

    companion object {
        const val OPENID = "openid"
        const val NICKNAME = "nickname";
        const val SEX = "sex"
        const val LANGUAGE = "language";
        const val HEADIMGURL = "headimgurl"
        const val COUNTRY = "country"
        const val PROVINCE = "province"
        const val CITY = "city"
        const val UNIONID = "unionid"
    }
}