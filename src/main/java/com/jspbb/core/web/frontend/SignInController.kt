package com.jspbb.core.web.frontend

import com.jspbb.core.Constants.FALLBACK_URL_PARAM
import com.jspbb.core.service.ConfigService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

/**
 * 登录Controller
 *
 * login为更常用的名称，facebook twitter github 都使用login作为登录名称。但 google 使用 signin signup，并且github虽然使用 login 作为 url，但链接名称还是使用 Sign in 和 Sign up。
 *
 * 更重要的是国内有些安全检测软件会把使用 login 作为登录 url 视为一种漏洞。
 */
@Controller
class SignInController(
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/sign_in")
    fun signInForm(request: HttpServletRequest, modelMap: Model): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/sign_in"
    }

    /**
     * 会经过[com.jspbb.core.security.ShiroAuthenticationFilter]，登录逻辑在该Filter中完成，具体请查看shiro官方文档。
     * 如果登录成功，会重定向至回调页面或首页，不会进入这个方法。
     * 如果登录失败，则会进入这个方法。
     */
    @PostMapping("/sign_in")
    fun signIn(username: String?, @RequestParam(FALLBACK_URL_PARAM) fallbackUrl: String?, request: HttpServletRequest, ra: RedirectAttributes): String {
        // 将登录异常类放入FlashAttribute
        var errorName = request.getAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME)
        if (errorName is String) {
            // 去除包名，获取最后的类名
            errorName = errorName.substring(errorName.lastIndexOf(".") + 1)
            ra.addFlashAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, errorName)
        } else if (SecurityUtils.getSubject().isAuthenticated) {
            // 用户已经登录
            return "redirect:/"
        }
        // 将用户名和回调页面放入FlashAttribute
        ra.addFlashAttribute("username", username)
        ra.addAttribute(FALLBACK_URL_PARAM, fallbackUrl)
        return "redirect:sign_in"
    }
}