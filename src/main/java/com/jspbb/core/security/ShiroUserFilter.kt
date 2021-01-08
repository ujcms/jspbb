package com.jspbb.core.security

import com.jspbb.core.Constants.CP
import org.apache.shiro.web.filter.authc.UserFilter
import org.apache.shiro.web.util.WebUtils
import java.io.IOException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Shiro用户Filter，处理没有权限时，根据请求地址决定重定向到前台或者后台登录页面。
 */
class ShiroUserFilter : UserFilter() {
    @Throws(IOException::class)
    override fun redirectToLogin(req: ServletRequest, resp: ServletResponse) {
        val request = req as HttpServletRequest
        val response = resp as HttpServletResponse
        var url: String = loginUrl
        if (request.requestURI == CP || request.requestURI.startsWith(request.contextPath + CP)) url = CP + loginUrl
        WebUtils.issueRedirect(request, response, url)
    }
}
