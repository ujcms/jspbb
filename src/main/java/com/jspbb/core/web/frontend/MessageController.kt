package com.jspbb.core.web.frontend

import com.jspbb.core.domain.Message
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.MessageService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class MessageController(
        private val userService: UserService,
        private val service: MessageService,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/messages")
    fun list(request: HttpServletRequest, response: HttpServletResponse): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/messages"
    }

    @GetMapping(Message.MESSAGES_CONTACT_URL)
    fun contact(@PathVariable contactUserId: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val contactUser = userService.select(contactUserId) ?: return Responses.notFound(response, "User ID not found: $contactUserId")
        service.readByContactUserId(user.id, contactUser.id)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        modelMap.addAttribute("contactUser", contactUser)
        return "$theme/messages_contact"
    }
}