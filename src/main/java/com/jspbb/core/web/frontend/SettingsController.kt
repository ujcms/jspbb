package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Created by PONY on 2017/4/29.
 */
@Controller
class SettingsController(
        private val deviceResolver: DeviceResolver,
        private val userService: UserService,
        private val configService: ConfigService
) {
    /**
     * 重定向至 /settings/profile
     */
    @GetMapping("/settings")
    fun index(): String {
        return "redirect:/settings/profile"
    }

    @GetMapping("/settings/profile")
    fun profileForm(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/settings/profile"
    }

    @GetMapping("/settings/picture")
    fun pictureForm(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/settings/picture"
    }

    @GetMapping("/settings/account")
    fun accountForm(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/settings/account"
    }
}
