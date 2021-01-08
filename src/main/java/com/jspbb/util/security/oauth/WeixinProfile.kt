package com.jspbb.util.security.oauth

import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.CITY
import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.COUNTRY
import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.LANGUAGE
import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.NICKNAME
import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.PROVINCE
import com.jspbb.util.security.oauth.WeixinProfileDefinition.Companion.UNIONID
import org.pac4j.oauth.profile.OAuth20Profile
import java.util.*

/**
 * 定义微信Profile的信息，有些是aouth共有的，有些是微信特有的。
 */
class WeixinProfile : OAuth20Profile() {
    override fun getLocale(): Locale? = getAttribute(LANGUAGE) as Locale?
    override fun getDisplayName(): String? = getAttribute(NICKNAME) as String?
    fun getCountry() = getAttribute(COUNTRY) as String?
    fun getProvince() = getAttribute(PROVINCE) as String?
    fun getCity() = getAttribute(CITY) as String?
    fun getUnionid() = getAttribute(UNIONID) as String?
}