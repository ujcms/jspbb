package com.jspbb.core.web.frontend

import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.NotificationService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class NotificationController(
        private val service: NotificationService,
        private val userService: UserService,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate;

    @GetMapping("/notifications")
    fun notification(request: HttpServletRequest, response: HttpServletResponse): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/notifications"
    }

//    @MessageMapping("/send_msg")
////    @SendTo("/topic/subscribe_msg")
//    fun sendMsg(msg: String) {
//
////        println("WebSocket: $msg; Principal: ${principal.name}")
//        println("WebSocket: $msg, username: ${Contexts.getUsername()}")
//        messagingTemplate.convertAndSendToUser(msg, "/queue/subscribe_msg", "$msg received.")
//
//    }
//
//    @SubscribeMapping("/subscribe_msg")
//    fun subscribe(): String {
//        println("感谢你订阅了我")
//        return "感谢你订阅了我"
//    }
}