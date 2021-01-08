package com.jspbb.util.security

/**
 * SHA256证书加密
 *
 * @author liufang
 */
class SHA256CredentialsDigest : HashCredentialsDigest(), CredentialsDigest {
    override fun digest(input: ByteArray, saltBytes: ByteArray?): ByteArray {
        return Digests.sha256(input, if (useSalt) saltBytes else null)
    }
}