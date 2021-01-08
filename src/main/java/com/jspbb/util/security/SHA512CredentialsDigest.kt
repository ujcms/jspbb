package com.jspbb.util.security

/**
 * SHA512证书加密
 *
 * @author liufang
 */
class SHA512CredentialsDigest : HashCredentialsDigest(), CredentialsDigest {
    override fun digest(input: ByteArray, saltBytes: ByteArray?): ByteArray {
        return Digests.sha512(input, if (useSalt) saltBytes else null)
    }
}