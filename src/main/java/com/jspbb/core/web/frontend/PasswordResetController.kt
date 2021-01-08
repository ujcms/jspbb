package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class PasswordResetController(
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/password_reset")
    fun passwordResetForm(request: HttpServletRequest): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/password_reset"
    }
}