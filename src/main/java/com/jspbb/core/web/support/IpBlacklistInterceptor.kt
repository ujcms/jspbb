package com.jspbb.core.web.support

import com.jspbb.core.domain.Configs
import com.jspbb.core.service.AnswerService
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
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
 * IP黑名单拦截器
 */
class IpBlacklistInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val configs = Configs(configService.selectAll())
        val ip = Servlets.getRemoteAddr(request)
        if (configs.restrict.inIpBlacklist(ip)) {
            Responses.forbidden(response, "IP in the blacklist: $ip")
            return false
        }
        return true
    }

    @Autowired
    private lateinit var configService: ConfigService
}