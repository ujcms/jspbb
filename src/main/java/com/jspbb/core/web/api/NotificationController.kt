package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.service.NotificationService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("apiNotificationController")
@RequestMapping("$API/notifications")
class NotificationController(
        private val service: NotificationService,
        private val userService: UserService
) {
    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate;

    @GetMapping("count")
    fun count(): Any {
        val username = Contexts.getUsername() ?: return 0
        val user = userService.selectByUsername(username) ?: return 0
        return service.countByUserId(user.id)
    }
}