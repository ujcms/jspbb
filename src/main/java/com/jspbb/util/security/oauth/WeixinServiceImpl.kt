package com.jspbb.util.security.oauth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.AbstractRequest
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthConfig
import com.github.scribejava.core.model.OAuthConstants
import com.github.scribejava.core.oauth.OAuth20Service

/**
 * 定义获取access_token的参数
 */
class WeixinServiceImpl(api: DefaultApi20, config: OAuthConfig) : OAuth20Service(api, config) {
    override fun <T : AbstractRequest> createAccessTokenRequest(code: String?, request: T): T {
        request.addParameter(WeixinApi.APPID, config.apiKey)
        request.addParameter(WeixinApi.SECRET, config.apiSecret)
        request.addParameter(OAuthConstants.CODE, code)
        request.addParameter(OAuthConstants.REDIRECT_URI, config.callback)
        val scope = config.scope
        if (scope != null) {
            request.addParameter(OAuthConstants.SCOPE, scope)
        }
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE)
        return request
    }

    override fun <T : AbstractRequest> createRefreshTokenRequest(refreshToken: String?, request: T): T {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw IllegalArgumentException("The refreshToken cannot be null or empty")
        }
        request.addParameter(WeixinApi.APPID, config.apiKey)
        request.addParameter(WeixinApi.SECRET, config.apiSecret)
        request.addParameter(OAuthConstants.REFRESH_TOKEN, refreshToken)
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.REFRESH_TOKEN)
        return request
    }

    override fun signRequest(accessToken: OAuth2AccessToken, request: AbstractRequest) {
        val jsonMapper = jacksonObjectMapper()
        val map: Map<String, String> = jsonMapper.readValue(accessToken.rawResponse)
        val openid = map[WeixinProfileDefinition.OPENID]
        request.addQuerystringParameter(WeixinProfileDefinition.OPENID, openid)
        super.signRequest(accessToken, request)
    }
}