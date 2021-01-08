package com.jspbb.core.security

import com.jspbb.core.service.UserService
import com.jspbb.util.security.CredentialsMatcherAdapter
import com.jspbb.util.security.HashCredentialsDigest
import org.apache.shiro.authc.*
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.util.ByteSource
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

/**
 * ShiroDbRealm
 *
 * @author liufang
 */
class ShiroDbRealm() : AuthorizingRealm(), InitializingBean {
    /**
     * 认证回调函数,登录时调用.
     */
    override fun doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo {
        token as UsernamePasswordToken
        val user = userService.selectByUsername(token.username)
        // 前后台登录共用，非管理员也可登录。
        when {
            user == null -> throw UnknownAccountException("No account found for user [${token.username}]")
            user.isNormal() -> return SimpleAuthenticationInfo(user.username, user.ext.password, ByteSource.Util.bytes(user.ext.salt), name)
            user.isLocked() -> throw LockedAccountException()
            else -> throw RuntimeException("User status unknown: ${user.status}")
        }
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        val auth = SimpleAuthorizationInfo()
        val username = getAvailablePrincipal(principals) as String
        val user = userService.selectByUsername(username)
        if (user != null && user.isNormal()) {
            auth.stringPermissions = user.getPermissions()
//            auth.addRole("user")
        }
        return auth
    }

    /**
     * 设定Password校验的Hash算法与迭代次数.
     */
    override fun afterPropertiesSet() {
        credentialsMatcher = CredentialsMatcherAdapter(credentialsDigest)
    }

    @Autowired
    private lateinit var credentialsDigest: HashCredentialsDigest
    @Autowired
    private lateinit var userService: UserService
}
