package com.jspbb.core.security.oauth

/**
 * Created by PONY on 2017.10.18.
 */
data class OAuthToken(
        var provider: String = "",
        var accessToken : String = "",
        var refreshToken : String = "",
        var openid: String = "",
        var displayName: String = "",
        /** m:男;f:女;n:未设置 */
        var gender : String = "n",
        var pictureUrl: String? = null,
        var largePictureUrl: String? = null,
        var unionid: String? = null
) {

}