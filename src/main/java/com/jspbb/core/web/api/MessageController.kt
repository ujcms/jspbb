package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.service.MessageService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController("apiMessageController")
@RequestMapping("$API/messages")
class MessageController(
        private val userService: UserService,
        private val service: MessageService
) {
    data class MessageParam(val toUserId: Long, val title: String?, val markdown: String)

    @PostMapping
    fun create(@RequestBody params: MessageParam, request: HttpServletRequest, response: HttpServletResponse): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val toUser = userService.select(params.toUserId) ?: return Responses.badRequest("User ID not found: ${params.toUserId}")
        if (toUser.id == user.id || toUser.id == 0L) return Responses.badRequest("toUserId cannot be self or 0: ${params.toUserId}")
        // 是否发帖过快
        if (!user.isMessageAllowed()) return Responses.badRequest(request, "error.postTooFast")
        service.send(user.id, toUser.id, toUser.username, params.title, params.markdown, Servlets.getRemoteAddr(request))
        return Responses.ok()
    }

    @DeleteMapping
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest, response: HttpServletResponse): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        for (id in ids) {
            service.select(id)?.let { message ->
                if (message.user.id == user.id) service.delete(id)
            }
        }
        return Responses.ok()
    }

    @DeleteMapping("contact")
    fun contactDelete(@RequestBody fromUserId: Long, request: HttpServletRequest, response: HttpServletResponse): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        service.deleteByFromUserId(user.id, fromUserId)
        return Responses.ok()
    }

    @GetMapping("receiver_suggest")
    fun messageReceiverSuggest(username: String?, request: HttpServletRequest): Any {
        val result = mutableListOf<Map<String, Any>>()
        if (username == null || username.isEmpty()) return result
        val currUsername = Contexts.getUsername() ?: return result
        val currUser = userService.selectByUsername(currUsername) ?: return result
        userService.messageReceiverSuggest(username, currUser.id).forEach { user ->
            result.add(mapOf("id" to user.id, "username" to user.username))
        }
        return result
    }

    @GetMapping("unread_count")
    fun messageUnreadCount(request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return 0
        val user = userService.selectByUsername(username) ?: return 0
        return service.countUnread(user.id)
    }
}