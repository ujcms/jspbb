package com.jspbb.util.security

/**
 * 证书加密
 *
 * @author liufang
 */
interface CredentialsDigest {
    fun digest(plainCredentials: String?, salt: String?): String?

    fun digest(plainCredentials: String?, saltBytes: ByteArray?): String?

    fun matches(credentials: String?, plainCredentials: String?, salt: String?): Boolean

    fun matches(credentials: String?, plainCredentials: String?, saltBytes: ByteArray?): Boolean
}
