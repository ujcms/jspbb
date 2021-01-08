package com.jspbb.util.security.oauth

import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuthConfig
import com.github.scribejava.core.model.OAuthConstants
import com.github.scribejava.core.model.ParameterList
import com.github.scribejava.core.oauth.OAuth20Service

/**
 * 微信API。定义登录授权地址、获取token地址、刷新token地址（可选）和登录授权地址参数。
 *
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN">微信网站登录开发指南</a>
 * @see <a href="https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316518&lang=zh_CN">微信获取用户级别信息</a>
 */
class WeixinApi : DefaultApi20() {
    override fun getAuthorizationBaseUrl(): String = "https://open.weixin.qq.com/connect/qrconnect"

    override fun getAccessTokenEndpoint(): String = "https://api.weixin.qq.com/sns/oauth2/access_token"

    override fun getRefreshTokenEndpoint(): String = "https://api.weixin.qq.com/sns/oauth2/refresh_token"

    override fun getAuthorizationUrl(config: OAuthConfig, additionalParams: Map<String, String>): String {
        val parameters = ParameterList(additionalParams)
        parameters.add(OAuthConstants.RESPONSE_TYPE, config.responseType)
        parameters.add(APPID, config.apiKey)

        val callback = config.callback
        if (callback != null) {
            parameters.add(OAuthConstants.REDIRECT_URI, callback)
        }

        val scope = config.scope
        if (scope != null) {
            parameters.add(OAuthConstants.SCOPE, scope)
        }

        val state = config.state
        if (state != null) {
            parameters.add(OAuthConstants.STATE, state)
        }

        return parameters.appendTo(authorizationBaseUrl)
    }

    override fun createService(config: OAuthConfig): OAuth20Service {
        return WeixinServiceImpl(this, config)
    }

    companion object {
        val INSTANCE = WeixinApi()
        /** client_id 在微信里面为 appid */
        const val APPID = "appid"
        /** client_secret 在微信里面为 secret */
        const val SECRET = "secret"
    }
}