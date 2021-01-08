package com.jspbb.util.security

import org.apache.commons.codec.binary.Hex
import org.apache.shiro.util.ByteSource

/**
 * Hash证书加密
 *
 * @author liufang
 */
abstract class HashCredentialsDigest : CredentialsDigest {

    override fun digest(plainCredentials: String?, salt: String?): String? {
        val saltBytes = salt?.let { ByteSource.Util.bytes(salt).bytes }
        return digest(plainCredentials, saltBytes)
    }

    override fun digest(plainCredentials: String?, saltBytes: ByteArray?): String? {
        if (plainCredentials == null) return null
        val plainCredentialsBytes = plainCredentials.toByteArray(Charsets.UTF_8)
        val hashPassword = digest(plainCredentialsBytes, saltBytes)
        return Hex.encodeHexString(hashPassword)
    }

    override fun matches(credentials: String?, plainCredentials: String?, salt: String?): Boolean {
        val saltBytes = salt?.let { ByteSource.Util.bytes(salt).bytes }
        return matches(credentials, plainCredentials, saltBytes)
    }

    override fun matches(credentials: String?, plainCredentials: String?, saltBytes: ByteArray?): Boolean {
        return if (credentials == null && plainCredentials == null) true else credentials == digest(plainCredentials, saltBytes)
    }

    protected abstract fun digest(input: ByteArray, saltBytes: ByteArray?): ByteArray

    var useSalt: Boolean = true

    companion object {
        const val HASH_INTERACTIONS = 1024
    }
}
