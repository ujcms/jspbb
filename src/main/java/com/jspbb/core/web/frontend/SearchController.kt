package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class SearchController(
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/search")
    fun form(request: HttpServletRequest): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/search"
    }
}