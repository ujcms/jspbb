package com.jspbb.util.security.oauth

import org.pac4j.core.context.WebContext
import org.pac4j.oauth.client.OAuth20Client

/**
 * 微信客户端。定义登录授权的scope。
 */
class WeixinClient : OAuth20Client<WeixinProfile> {
    constructor()
    constructor(key: String, secret: String) {
        setKey(key)
        setSecret(secret)
    }

    override fun clientInit(context: WebContext?) {
        configuration.api = WeixinApi.INSTANCE
        configuration.profileDefinition = WeixinProfileDefinition()
        configuration.scope = scope
        configuration.isWithState = true
        setConfiguration(configuration)
        super.clientInit(context)
    }

    var scope: String = DEFAULT_SCOPE

    companion object {
        const val DEFAULT_SCOPE = "snsapi_login"
    }
}