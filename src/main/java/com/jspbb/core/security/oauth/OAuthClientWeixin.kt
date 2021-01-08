package com.jspbb.core.security.oauth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.scribejava.core.model.ParameterList
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder

/**
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN">微信网站登录开发指南</a>
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316518&lang=zh_CN">微信获取用户级别信息</a>
 */
class OAuthClientWeixin(key: String, secret: String, callback: String, scope: String) : OAuthClient(key, secret, callback, scope) {
    constructor(key: String, secret: String, callback: String) : this(key, secret, callback, DEFAULT_SCOPE)

    override fun getAuthorizationBaseUrl() = "https://open.weixin.qq.com/connect/qrconnect"

    override fun getAuthorizationUrl(state: String): String {
        val parameters = ParameterList()
        parameters.add(APPID, key)
        parameters.add(REDIRECT_URI, callback)
        parameters.add(RESPONSE_TYPE, RESPONSE_TYPE_CODE)
        // 必填
        parameters.add(SCOPE, scope ?: DEFAULT_SCOPE)
        parameters.add(STATE, state)
        return parameters.appendTo(getAuthorizationBaseUrl())
    }

    override fun getAccessTokenEndpoint(): String = "https://api.weixin.qq.com/sns/oauth2/access_token"

    override fun getOAuthToken(code: String): OAuthToken {
        val builder = URIBuilder(getAccessTokenEndpoint())
        builder.setParameter(APPID, key)
        builder.setParameter(SECRET, secret)
        builder.setParameter(GRANT_TYPE, AUTHORIZATION_CODE)
        builder.setParameter(CODE, code)
//        builder.setParameter(REDIRECT_URI, callback)
        val response = executeHttp(HttpGet(builder.build()))
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, String> = jsonMapper.readValue(response)
        if (map[ACCESS_TOKEN] == null) {
            throw RuntimeException("weixin get_access_token error: $response")
        }
        val token = OAuthToken(provider = PROVIDER)
        token.accessToken = map[ACCESS_TOKEN]!!
        token.refreshToken = map[REFRESH_TOKEN]!!
        token.openid = map[OPENID]!!
        token.unionid = map[UNIONID]
        fillProfile(token)
        return token
    }

    private fun getProfileUrl() = "https://api.weixin.qq.com/sns/userinfo"

    private fun fillProfile(token: OAuthToken) {
        val builder = URIBuilder(getProfileUrl())
        builder.setParameter(ACCESS_TOKEN, token.accessToken)
        builder.setParameter(OPENID, token.openid)
        val response = executeHttp(HttpGet(builder.build()))
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, Any> = jsonMapper.readValue(response)
        if (map[NICKNAME] == null) {
            throw RuntimeException("weixin get_userinfo error: $response")
        }
        token.displayName = map[NICKNAME] as String
        val sex = map[SEX] as Int
        token.gender = if (sex == 1) "m" else if (sex == 2) "f" else "n"
        token.pictureUrl = map[HEADIMGURL] as String
        token.largePictureUrl = map[HEADIMGURL] as String
    }

//    override fun getRefreshTokenEndpoint(): String = "https://api.weixin.qq.com/sns/oauth2/refresh_token"

    override fun getProviderPrefix() = "weixin"

    companion object {
        const val PROVIDER = "weixin"
        /** client_id 在微信里面为 appid */
        const val APPID = "appid"
        /** client_secret 在微信里面为 secret */
        const val SECRET = "secret"
        const val DEFAULT_SCOPE = "snsapi_login"
        const val OPENID = "openid"
        const val UNIONID = "unionid"
        const val NICKNAME = "nickname"
        // headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
        const val HEADIMGURL = "headimgurl"
        const val SEX = "sex"
    }

}