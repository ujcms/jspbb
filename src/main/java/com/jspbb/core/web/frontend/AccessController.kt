package com.jspbb.core.web.frontend

import com.jspbb.core.Constants.IDENTITY_COOKIE_NAME
import com.jspbb.core.domain.User
import com.jspbb.core.service.AccessService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.util.web.Servlets
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 访问Controller
 */
@Controller
class AccessController(
        private val userService: UserService,
        private val accessService: AccessService
) {
    @GetMapping("/access")
    fun access(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model) {
        var user: User? = null
        Contexts.getUsername()?.let {
            user = userService.selectByUsername(it)
        }
        val queryMap = Servlets.parseQueryString(request.queryString)
        var url = Servlets.getParam(queryMap, "url")
        // url 地址不存在，则不记录
        if (url.isNullOrBlank()) return
        var referrer = Servlets.getParam(queryMap, "referrer")
        var userAgent = request.getHeader("user-agent")
        // 不超过最大长度
        url = StringUtils.substring(url, 0, 255)
        referrer = StringUtils.substring(referrer, 0, 255)
        userAgent = StringUtils.substring(userAgent, 0, 450)
        val ip = Servlets.getRemoteAddr(request)
        val cookie = Servlets.identityCookie(IDENTITY_COOKIE_NAME, request, response)
        accessService.insert(url, referrer, userAgent, ip, cookie, user)
    }
}
