package com.jspbb.core.web.frontend

import com.jspbb.core.domain.*
import com.jspbb.core.service.*
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
class QuestionController(
        private val notificationService: NotificationService,
        private val userService: UserService,
        private val answerService: AnswerService,
        private val service: QuestionService,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/questions/{id}")
    fun showQuestion(@PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val question = service.select(id) ?: return Responses.notFound(response, "Question not found: $id")
        if (!question.isNormal()) return Responses.notFound(response, "Question status not normal: $id")
        question.views++
        service.update(question, question.ext)
        modelMap.addAttribute("question", question)
        // 删除通知
        Contexts.getUsername()?.let { username ->
            userService.selectByUsername(username)?.let { user ->
                // data 都使用question.id，可以一起删除
                val types = arrayOf(Question.NOTIFICATION_TYPE, Question.NOTIFICATION_UPDATE_TYPE, Answer.NOTIFICATION_TYPE, Answer.NOTIFICATION_UPDATE_TYPE, Comment.NOTIFICATION_TYPE, Comment.NOTIFICATION_UPDATE_TYPE)
                notificationService.deleteByTypesAndData(user.id, types, question.id.toString())
            }
        }
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/questions_show"
    }

    @GetMapping("/questions/{id}/answers/{answerId}")
    fun showAnswer(@PathVariable id: Long, @PathVariable answerId: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val question = service.select(id) ?: return Responses.notFound(response, "Question not found: $id")
        val answer = answerService.select(answerId) ?: return "redirect:/question/$id"
        if (answer.questionId != id) return "redirect:/question/$id"
        question.views++
        service.update(question, question.ext)
        modelMap.addAttribute("question", question)
        modelMap.addAttribute("answer", answer)
        // 删除通知
        Contexts.getUsername()?.let { username ->
            userService.selectByUsername(username)?.let { user ->
                // data 都使用question.id，可以一起删除
                val types = arrayOf(Question.NOTIFICATION_TYPE, Question.NOTIFICATION_UPDATE_TYPE, Answer.NOTIFICATION_TYPE, Answer.NOTIFICATION_UPDATE_TYPE, Comment.NOTIFICATION_TYPE, Comment.NOTIFICATION_UPDATE_TYPE)
                notificationService.deleteByTypesAndData(user.id, types, question.id.toString())
            }
        }
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/questions_show"
    }

    @GetMapping("/questions/new")
    fun add(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/questions_form"
    }

    @GetMapping("/questions/{id}/edit")
    fun edit(@PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized(request, response)
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized(request, response)
        val question = service.select(id) ?: return Responses.notFound(response, "Question not found: $id")
        // 是否有权限
        if (!user.hasPerm(Group.PERM_QUESTION_EDIT, question.userId)) return Responses.forbidden(response)
        modelMap.addAttribute("question", question)
        modelMap.addAttribute("editing", true)
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/questions_form"
    }
}