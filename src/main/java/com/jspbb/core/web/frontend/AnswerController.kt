package com.jspbb.core.web.frontend

import com.jspbb.core.domain.Group
import com.jspbb.core.service.AnswerService
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.QuestionService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class AnswerController(
        private val userService: UserService,
        private val service: AnswerService,
        private val questionService: QuestionService,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/answers/new")
    fun add(questionId: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        val question = questionService.select(questionId)
                ?: return Responses.notFound(response, "Question not found: $questionId")
        modelMap.addAttribute("question", question)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/answers_form"
    }

    @GetMapping("/answers/edit")
    fun edit(id: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val answer = service.select(id) ?: return Responses.notFound(response, "Answer not found: $id")
        if (!user.hasPerm(Group.PERM_ANSWER_EDIT, answer.userId)) return Responses.forbidden(response)
        modelMap.addAttribute("answer", answer)
        modelMap.addAttribute("question", answer.question)
        modelMap.addAttribute("editing", true)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/answers_form"
    }
}