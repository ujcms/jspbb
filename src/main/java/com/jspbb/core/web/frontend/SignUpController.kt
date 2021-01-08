package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

/**
 * Created by PONY on 2017/4/29.
 */
@Controller
class SignUpController(
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/sign_up")
    fun signUp(request: HttpServletRequest, modelMap: Model): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/sign_up"
    }

}
