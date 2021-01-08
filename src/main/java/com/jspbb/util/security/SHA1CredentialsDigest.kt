package com.jspbb.util.security

/**
 * SHA1证书加密
 *
 * @author liufang
 */
class SHA1CredentialsDigest : HashCredentialsDigest() {
    override fun digest(input: ByteArray, saltBytes: ByteArray?): ByteArray {
        return Digests.sha1(input, if (useSalt) saltBytes else null)
    }
}
