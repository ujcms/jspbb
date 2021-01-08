package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Comment
import com.jspbb.core.domain.Group
import com.jspbb.core.domain.Question
import com.jspbb.core.domain.QuestionExt
import com.jspbb.core.service.QuestionService
import com.jspbb.core.service.SensitiveService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController("apiQuestionController")
@RequestMapping("$API/questions")
class QuestionController(
        private val sensitiveService: SensitiveService,
        private val userService: UserService,
        private val service: QuestionService
) {
    data class QuestionParam(val id: Long?, val title: String, val markdown: String?)

    @PostMapping
    fun create(@RequestBody params: QuestionParam, request: HttpServletRequest): Any? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // 是否发帖过快
        if (!user.isQuestionAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val question = Question(userId = user.id, user = user, activeUserId = user.id, activeUser = user,
                ext = QuestionExt(title = params.title, markdown = params.markdown, ip = Servlets.getRemoteAddr(request)))
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(question.ext.title) || sensitiveService.matches(question.ext.markdown)) question.status = Question.STATUS_SHIELDED
        service.insert(question, question.ext)
        // 记录发帖时间
        userService.addUpQuestion(user)
        // 状态不正常代表包含敏感词
        if (!question.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        return question
    }

    @PutMapping
    fun update(@RequestBody params: QuestionParam, request: HttpServletRequest): Any? {
        val id = params.id ?: return Responses.badRequest("Role update 'id' must not be null")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val question = service.select(id) ?: return Responses.badRequest("Question not found: $id")
        // 是否有权限
        if (!user.hasPerm(Group.PERM_QUESTION_EDIT, question.userId)) return Responses.forbidden()
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(question.ext.title) || sensitiveService.matches(question.ext.markdown)) question.status = Question.STATUS_SHIELDED
        service.update(question, question.ext, params.title, params.markdown, Servlets.getRemoteAddr(request), user.id)
        // 状态不正常代表包含敏感词
        if (!question.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        return question
    }

    @DeleteMapping
    fun delete(@RequestBody ids: Array<Long>, request: HttpServletRequest): Any? {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        if (!user.hasPerm(Group.PERM_QUESTION_DELETE)) return Responses.forbidden()
        ids.forEach { service.deleteLogical(it) }
        return Responses.ok()
    }
}