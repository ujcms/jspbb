package com.jspbb.util.security.csrf

import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 防止 CSRF 攻击。除了"GET","HEAD","TRACE","OPTIONS" 方式的请求，都需要提供csrf token，否则不允许访问。
 */
class CsrfFilter() : OncePerRequestFilter() {
    constructor(vararg excludeUrls: String) : this() {
        this.excludeUrls.addAll(excludeUrls)
    }

    var excludeUrls: MutableList<String> = ArrayList();
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        var token = CsrfToken.loadTokenFromCookie(request, response)
        val missingToken = token == null
        if (token == null) {
            token = UUID.randomUUID().toString()
            CsrfToken.saveToken(token, request, response)
        }
        request.setAttribute(CsrfToken.PARAMETER_NAME, CsrfToken(token))
        // 判断访问地址是否需要检查
        val servletPath = request.servletPath
        var exclude = false
        for (excludeUrl in excludeUrls) {
            if (servletPath.startsWith(excludeUrl)) {
                exclude = true
            }
        }
        // GET 等方式不用提供Token，自动放行，不能用于修改数据。修改数据必须使用 POST、PUT、DELETE、PATCH 方式并提供Token。
        if (!listOf("GET", "HEAD", "TRACE", "OPTIONS").contains(request.method) && !exclude) {
            val actualToken = request.getHeader(CsrfToken.HEADER_NAME) ?: request.getParameter(CsrfToken.PARAMETER_NAME)
            if (token != actualToken) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, if (missingToken) "CSRF Token Missing" else "CSRF Token Invalid")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}