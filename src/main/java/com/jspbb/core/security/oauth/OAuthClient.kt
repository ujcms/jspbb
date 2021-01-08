package com.jspbb.core.security.oauth

import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.HttpURLConnection
import javax.servlet.http.HttpServletResponse

/**
 *
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN">微信网站登录开发指南</a>
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316518&lang=zh_CN">微信获取用户级别信息</a>
 *
 * @see <a href="http://wiki.connect.qq.com/%e4%bd%bf%e7%94%a8authorization_code%e8%8e%b7%e5%8f%96access_token">QQ互联网站登录: 获取access_token</a>
 * @see <a href="http://wiki.connect.qq.com/%e8%8e%b7%e5%8f%96%e7%94%a8%e6%88%b7openid_oauth2-0">QQ互联网站登录: 获取用openid</a>
 * @see <a href="http://wiki.connect.qq.com/get_user_info">QQ互联网站登录: 获取用户信息</a>
 *
 * @see <a href="http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI">微博API</a>
 * @see <a href="http://open.weibo.com/wiki/Oauth2/authorize">微博API: oauth2/authorize</a>
 * @see <a href="http://open.weibo.com/wiki/Oauth2/access_token">微博API: oauth2/access_token</a>
 * @see <a href="http://open.weibo.com/wiki/2/users/show">微博API: users/show</a>
 *
 */
abstract class OAuthClient(val key: String, val secret: String, val callback: String, val scope: String? = null) {
    abstract fun getAuthorizationBaseUrl(): String

    open fun getAuthorizationUrl(state: String): String {
        val builder = URIBuilder(getAuthorizationBaseUrl())
                .setParameter(RESPONSE_TYPE, RESPONSE_TYPE_CODE)
                .setParameter(CLIENT_ID, key)
                .setParameter(REDIRECT_URI, callback)
        if (scope != null) {
            builder.setParameter(SCOPE, scope)
        }
        builder.setParameter(STATE, state)
        return builder.build().toString()
    }

    protected fun executeHttp(request: HttpUriRequest): String {
        val httpclient = HttpClients.createDefault()
        httpclient.execute(request).use { response ->
            if (response.statusLine.statusCode != HttpServletResponse.SC_OK) {
                throw RuntimeException("http request error, response status code: ${response.statusLine.statusCode}. request uri: ${request.uri}")
            }
            return EntityUtils.toString(response.entity, Charsets.UTF_8)
        }
    }

    abstract fun getAccessTokenEndpoint(): String

    abstract fun getOAuthToken(code: String): OAuthToken

    /**
     * 提供者前缀。如果有联合ID，则返回提供者前缀，否则返回null
     */
    open fun getProviderPrefix(): String? = null

//    abstract fun getRefreshTokenEndpoint(): String

    private var connection: HttpURLConnection? = null

    companion object {
        const val SCOPE = "scope"
        // OAuth 2.0
        const val ACCESS_TOKEN = "access_token"
        const val CLIENT_ID = "client_id"
        const val CLIENT_SECRET = "client_secret"
        const val REDIRECT_URI = "redirect_uri"
        const val CODE = "code"
        const val REFRESH_TOKEN = "refresh_token"
        const val GRANT_TYPE = "grant_type"
        const val AUTHORIZATION_CODE = "authorization_code"
        const val STATE = "state"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val RESPONSE_TYPE = "response_type"
        const val RESPONSE_TYPE_CODE = "code"

        const val DISPLAY_NAME = "display_name"
        const val GENDER = "gender"
        const val PICTURE_URL = "picture_url";
    }
}