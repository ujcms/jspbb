package com.jspbb.util.security

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authc.credential.CredentialsMatcher

/**
 * 证书加密适配器
 *
 * @author liufang
 */
class CredentialsMatcherAdapter(
        private val credentialsDigest: CredentialsDigest
) : CredentialsMatcher {
    override fun doCredentialsMatch(token: AuthenticationToken, info: AuthenticationInfo): Boolean {
        token as UsernamePasswordToken; info as SimpleAuthenticationInfo
        val plainCredentials = token.password?.let { String(it) }
        return credentialsDigest.matches(info.credentials as String, plainCredentials, info.credentialsSalt.bytes)
    }
}
