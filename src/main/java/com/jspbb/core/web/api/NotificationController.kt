package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.service.NotificationService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController("apiNotificationController")
@RequestMapping("$API/notifications")
class NotificationController(
        private val service: NotificationService,
        private val userService: UserService
) {
    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate;

    @GetMapping
    fun list(limit: Int?, request: HttpServletRequest): Any? {
        val username = Contexts.getUsername() ?: return "[]"
        val user = userService.selectByUsername(username) ?: return "[]"
        val queryMap = HashMap<String, Any>()
        queryMap["EQ_userId"] = user.id
        return service.selectList(queryMap, if (limit == null || limit < 0) 8 else limit)
    }

    @DeleteMapping
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // ids为空全部删除
        if (ids.isEmpty()) service.deleteByUserId(user.id)
        for (id in ids) service.delete(id)
        return Responses.ok()
    }

    @GetMapping("count")
    fun count(): Any {
        val username = Contexts.getUsername() ?: return 0
        val user = userService.selectByUsername(username) ?: return 0
        return service.countByUserId(user.id)
    }
}