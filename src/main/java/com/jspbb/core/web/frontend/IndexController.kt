package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Created by PONY on 2017/4/29.
 */
@Controller
class IndexController(
        private val userService: UserService,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/")
    fun index(request: HttpServletRequest, modelMap: Model): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/index"
    }

    @GetMapping("/terms")
    fun terms(request: HttpServletRequest, modelMap: Model): String {
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/terms"
    }

    @GetMapping("/users/{id}")
    fun user(@PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val targetUser = userService.select(id) ?: return Responses.notFound(response, "User id not found: $id")
        modelMap.addAttribute("targetUser", targetUser)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/user"
    }

    @GetMapping("/u/{home}")
    fun home(@PathVariable home: String, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val targetUser = userService.selectByHome(home) ?: return Responses.notFound(response, "User home not found: $home")
        modelMap.addAttribute("targetUser", targetUser)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/user"
    }

//    @GetMapping(path = ["/events"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
//    fun handle(request: HttpServletRequest): SseEmitter {
//        // Save the emitter somewhere..
//        val emitter  = SseEmitter()
//        request.servletContext.setAttribute("emitter", emitter )
//        return emitter
//    }
}
