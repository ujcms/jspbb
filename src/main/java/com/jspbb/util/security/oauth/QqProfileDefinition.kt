package com.jspbb.util.security.oauth

import com.github.scribejava.core.model.OAuth2AccessToken
import org.pac4j.core.profile.converter.Converters
import org.pac4j.oauth.config.OAuth20Configuration
import org.pac4j.oauth.profile.JsonHelper
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition

/**
 * Created by PONY on 2017.10.31.
 */
class QqProfileDefinition : OAuth20ProfileDefinition<WeixinProfile>({ WeixinProfile() }) {
    init {
        listOf<String>(NICKNAME, COUNTRY, PROVINCE, CITY, UNIONID).forEach {
            primary(it, Converters.STRING)
        }
        //TODO 微信是 1:男,2:女，转换器是 1:女,2:男，要自定义转换器
        primary(SEX, Converters.GENDER)
        primary(HEADIMGURL, Converters.URL)
        primary(LANGUAGE, Converters.LOCALE)
    }

    override fun getProfileUrl(accessToken: OAuth2AccessToken?, configuration: OAuth20Configuration?): String {
        return "https://api.weixin.qq.com/cgi-bin/user/info"
    }

    override fun extractUserProfile(body: String?): WeixinProfile {
        val profile = newProfile()
        val json = JsonHelper.getFirstNode(body)
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "openid"))
            for (attribute in primaryAttributes) {
                convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute))
            }
        }
        return profile
    }

    companion object {
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