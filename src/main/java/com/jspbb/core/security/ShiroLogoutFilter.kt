package com.jspbb.core.security

import com.jspbb.core.Constants.CP
import com.jspbb.core.Constants.FALLBACK_URL_PARAM
import org.apache.shiro.subject.Subject
import org.apache.shiro.web.filter.authc.LogoutFilter
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * Shiro退出登录Filter，处理返回页面。
 * <p>
 * 如果存在fallbackUrl参数，则根据参数返回，否则根据请求路径分别返回前台首页或后台首页。
 */
class ShiroLogoutFilter : LogoutFilter() {
    @Throws(Exception::class)
    override fun preHandle(request: ServletRequest?, response: ServletResponse?): Boolean {
        // TODO 写入退出登录日志。或许应该放到Session过期的地方写会更好。
        //		Subject subject = getSubject(request, response);
        //		Object principal = subject.getPrincipal();
        //		String ip = Servlets.getRemoteAddr(request);
        //		if (principal != null) {
        //			getOperationLogService().logout(ip, principal.toString());
        //		}
        return super.preHandle(request, response)
    }

    override fun getRedirectUrl(req: ServletRequest?, resp: ServletResponse?, subject: Subject?): String {
        val request = req as HttpServletRequest
        var fallbackUrl = request.getParameter(FALLBACK_URL_PARAM)
        if (fallbackUrl.isNullOrBlank()) return redirectUrl
        if (fallbackUrl.length > request.contextPath.length) {
            fallbackUrl = fallbackUrl.substring(request.contextPath.length)
        }
        return fallbackUrl
    }
}
