package com.jspbb.util.security

/**
 * SHA1证书加密
 *
 * @author liufang
 */
class MD5CredentialsDigest : HashCredentialsDigest() {
    override fun digest(input: ByteArray, saltBytes: ByteArray?): ByteArray {
        return Digests.md5(input, if (useSalt) saltBytes else null)
    }
}
