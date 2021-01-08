package com.jspbb.core.security.oauth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.scribejava.core.model.ParameterList
import org.apache.http.Consts
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.utils.URLEncodedUtils

/**
 * @see <a href="http://wiki.connect.qq.com/%e4%bd%bf%e7%94%a8authorization_code%e8%8e%b7%e5%8f%96access_token">QQ互联网站登录: 获取access_token</a>
 * @see <a href="http://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96%e7%94%a8%e6%88%b7openid_oauth2-0">QQ互联网站登录: 获取用openid</a>
 * @see <a href="http://wiki.connect.qq.com/get_user_info">QQ互联网站登录: 获取用户信息</a>
 */
class OAuthClientQq(key: String, secret: String, callback: String, scope: String?) : OAuthClient(key, secret, callback, scope) {
    constructor(key: String, secret: String, callback: String) : this(key, secret, callback, null)

    override fun getAuthorizationBaseUrl() = "https://graph.qq.com/oauth2.0/authorize"

    override fun getAuthorizationUrl(state: String): String {
        val parameters = ParameterList()
        parameters.add(CLIENT_ID, key)
        parameters.add(REDIRECT_URI, callback)
        parameters.add(RESPONSE_TYPE, RESPONSE_TYPE_CODE)
        if (scope != null) {
            parameters.add(SCOPE, scope)
        }
        parameters.add(STATE, state)
        return parameters.appendTo(getAuthorizationBaseUrl())
    }

    override fun getAccessTokenEndpoint(): String = "https://graph.qq.com/oauth2.0/token"

    override fun getOAuthToken(code: String): OAuthToken {
        val builder = URIBuilder(getAccessTokenEndpoint())
        builder.setParameter(CLIENT_ID, key)
        builder.setParameter(CLIENT_SECRET, secret)
        builder.setParameter(GRANT_TYPE, AUTHORIZATION_CODE)
        builder.setParameter(CODE, code)
        builder.setParameter(REDIRECT_URI, callback)
        val response = executeHttp(HttpGet(builder.build()))
        val token = OAuthToken(provider = PROVIDER)
        for (item in URLEncodedUtils.parse(response, Consts.UTF_8)) {
            if (item.name == ACCESS_TOKEN) token.accessToken = item.value
            if (item.name == REFRESH_TOKEN) token.refreshToken = item.value
        }
        if (token.accessToken == "") {
            throw RuntimeException("qq get_access_token error: $response")
        }
        token.openid = getOpenid(token.accessToken)
        fillProfile(token)
        return token
    }

    private fun getOpenid(accessToken: String): String {
        val builder = URIBuilder("https://graph.qq.com/oauth2.0/me")
        builder.setParameter(ACCESS_TOKEN, accessToken)
        // 返回内容为 `callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} ); `
        var response = executeHttp(HttpGet(builder.build()))
        // 没有"openid"字符代表获取失败
        if (response.indexOf("\"openid\"") == -1) {
            throw RuntimeException("qq get_openid error: $response")
        }
        response = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1)
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, String> = jsonMapper.readValue(response)
        return map[OPENID]!!
    }

    private fun getProfileUrl() = "https://graph.qq.com/user/get_user_info"

    private fun fillProfile(token: OAuthToken) {
        val builder = URIBuilder(getProfileUrl())
        builder.setParameter("oauth_consumer_key", key)
        builder.setParameter(ACCESS_TOKEN, token.accessToken)
        builder.setParameter(OPENID, token.openid)
        val response = executeHttp(HttpGet(builder.build()))
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, Any> = jsonMapper.readValue(response)
        if (map[NICKNAME] == null) {
            throw RuntimeException("qq get_user_info error: $response")
        }
        token.displayName = map[NICKNAME] as String
        token.gender = if (map[GENDER] == "男") "m" else if (map[GENDER] == "女") "f" else "n"
        token.pictureUrl = (map[FIGUREURL_QQ_1]) as String
        token.largePictureUrl = (map[FIGUREURL_QQ_2] ?: map[FIGUREURL_QQ_1]) as String
    }

//    override fun getRefreshTokenEndpoint(): String = "https://graph.qq.com/oauth2.0/token"

    companion object {
        const val PROVIDER = "qq"
        const val DEFAULT_SCOPE = "get_user_info"
        const val OPENID = "openid"
        const val NICKNAME = "nickname"
        // figureurl_qq_1	大小为40×40像素的QQ头像URL。
        const val FIGUREURL_QQ_1 = "figureurl_qq_1"
        // figureurl_qq_2	大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
        const val FIGUREURL_QQ_2 = "figureurl_qq_2"
    }

}