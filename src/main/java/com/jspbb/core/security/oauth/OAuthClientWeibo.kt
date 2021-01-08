package com.jspbb.core.security.oauth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.scribejava.core.model.ParameterList
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder

/**
 * @see <a href="http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI">微博API</a>
 * @see <a href="http://open.weibo.com/wiki/Oauth2/authorize">微博API: oauth2/authorize</a>
 * @see <a href="http://open.weibo.com/wiki/Oauth2/access_token">微博API: oauth2/access_token</a>
 * @see <a href="http://open.weibo.com/wiki/2/users/show">微博API: users/show</a>
 */
class OAuthClientWeibo(key: String, secret: String, callback: String, scope: String?) : OAuthClient(key, secret, callback, scope) {
    constructor(key: String, secret: String, callback: String) : this(key, secret, callback, null)

    override fun getAuthorizationBaseUrl() = "https://api.weibo.com/oauth2/authorize"

    override fun getAuthorizationUrl(state: String): String {
        val parameters = ParameterList()
        parameters.add(CLIENT_ID, key)
        parameters.add(REDIRECT_URI, callback)
//        parameters.add(RESPONSE_TYPE, RESPONSE_TYPE_CODE)
        if (scope != null) {
            parameters.add(SCOPE, scope)
        }
        parameters.add(STATE, state)
        return parameters.appendTo(getAuthorizationBaseUrl())
    }

    override fun getAccessTokenEndpoint(): String = "https://api.weibo.com/oauth2/access_token"

    override fun getOAuthToken(code: String): OAuthToken {
        val builder = URIBuilder(getAccessTokenEndpoint())
        builder.setParameter(CLIENT_ID, key)
        builder.setParameter(CLIENT_SECRET, secret)
        builder.setParameter(GRANT_TYPE, AUTHORIZATION_CODE)
        builder.setParameter(CODE, code)
        builder.setParameter(REDIRECT_URI, callback)
        // 微博要求该api必须使用http post请求，否则出错。
        val response = executeHttp(HttpPost(builder.build()))
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, String> = jsonMapper.readValue(response)
        if (map[ACCESS_TOKEN] == null) {
            throw RuntimeException("weibo get_access_token error: $response")
        }
        val token = OAuthToken(provider = PROVIDER)
        token.accessToken = map[ACCESS_TOKEN]!!
        token.openid = map[UID]!!
        fillProfile(token)
        return token
    }

    private fun getProfileUrl() = "https://api.weibo.com/2/users/show.json"

    private fun fillProfile(token: OAuthToken) {
        val builder = URIBuilder(getProfileUrl())
        builder.setParameter("oauth_consumer_key", key)
        builder.setParameter(ACCESS_TOKEN, token.accessToken)
        builder.setParameter(UID, token.openid)
        val response = executeHttp(HttpGet(builder.build()))
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, Any> = jsonMapper.readValue(response)
        if (map[SCREEN_NAME] == null) {
            throw RuntimeException("weibo get_user_info error: $response")
        }
        token.displayName = map[SCREEN_NAME] as String
        token.gender = map[GENDER] as String
        token.pictureUrl = map[PROFILE_IMAGE_URL] as String
        token.largePictureUrl = (map[AVATAR_HD] ?: map[AVATAR_LARGE] ?: map[PROFILE_IMAGE_URL]) as String
    }

    companion object {
        const val PROVIDER = "weibo"
        const val UID = "uid"
        const val SCREEN_NAME = "screen_name"
        // profile_image_url string 用户头像地址（中图），50×50像素
        const val PROFILE_IMAGE_URL = "profile_image_url"
        // avatar_large string 用户头像地址（大图），180×180像素
        const val AVATAR_LARGE = "avatar_large"
        // avatar_hd string 用户头像地址（高清），高清头像原图
        const val AVATAR_HD = "avatar_hd"
    }

}