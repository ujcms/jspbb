package com.jspbb.core.web.support

import com.jspbb.core.domain.Configs
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.util.web.Servlets
import org.apache.commons.lang3.StringUtils
import org.apache.shiro.SecurityUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mobile.device.DeviceResolver
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 注册拦截器时，已设置所有带点号的文件名都不会经过拦截器，如 jquery.js bootstrap.min.css
 */
class FrontendInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 设置设置当前登录用户，页面可以通过用户是否存在来判断用户是否登录
        val subject = SecurityUtils.getSubject()
        val principal = subject.principal
        if (principal is String && (subject.isRemembered || subject.isAuthenticated)) {
            userService.selectByUsername(principal)?.let { user ->
                if (user.status == 0) {
                    request.setAttribute("user", user)
                    request.session.setAttribute("user", user)
                } else {
                    // 用户被锁定则退出登录
                    subject.logout()
                }
            }
        }
        return true
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        // 当有返回模板时才设置相关数据
        if (modelAndView?.viewName?.isNotBlank() == true) {
            val configs = Configs(configService.selectAll())
            request.setAttribute("configs", configs)
            // 设置当前请求URL地址
            request.setAttribute("url", Servlets.getCurrentUrl(request))
            // 设置当前页码
            request.setAttribute("page", request.getParameter("page")?.toIntOrNull() ?: 1)
            // 设置模板主题
            request.setAttribute("theme", configs.getTheme(request, deviceResolver))
        }
    }

    @Autowired
    private lateinit var deviceResolver: DeviceResolver
    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var configService: ConfigService
}