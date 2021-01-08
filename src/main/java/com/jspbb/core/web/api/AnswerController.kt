package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Answer
import com.jspbb.core.domain.AnswerExt
import com.jspbb.core.domain.Group
import com.jspbb.core.domain.Question
import com.jspbb.core.service.AnswerService
import com.jspbb.core.service.QuestionService
import com.jspbb.core.service.SensitiveService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController("apiAnswerController")
@RequestMapping("$API/answers")
class AnswerController(
        private val sensitiveService: SensitiveService,
        private val userService: UserService,
        private val questionService: QuestionService,
        private val service: AnswerService
) {
    data class AnswerParam(val id: Long?, val questionId: Long?, val markdown: String);

    @PostMapping
    fun create(@RequestBody params: AnswerParam, request: HttpServletRequest): Any {
        val questionId = params.questionId ?: return Responses.badRequest("Answer questionId cannot be null")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val question = questionService.select(questionId) ?: return Responses.badRequest("Question not found: $questionId")
        // 是否发帖过快
        if (!user.isAnswerAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val answer = Answer(questionId = questionId, question = question, userId = user.id, user = user,
                ext = AnswerExt(markdown = params.markdown, ip = Servlets.getRemoteAddr(request)))
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(answer.ext.markdown)) answer.status = Answer.STATUS_SHIELDED
        service.insert(answer, answer.ext)
        // 记录发帖时间
        userService.addUpAnswer(user)
        // 状态不正常代表包含敏感词
        if (!answer.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        return answer
    }

    @PutMapping
    fun update(@RequestBody params: AnswerParam, request: HttpServletRequest): Any {
        val id = params.id ?: return Responses.badRequest("Answer id cannot be null")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val answer = service.select(id) ?: return Responses.badRequest("Answer not found: $id")
        // 只有自己和超级管理员可以修改
        if (!user.hasPerm(Group.PERM_ANSWER_EDIT, answer.userId)) return Responses.forbidden()
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(answer.ext.markdown)) answer.status = Answer.STATUS_SHIELDED
        service.update(answer, answer.ext, params.markdown, Servlets.getRemoteAddr(request), user.id)
        // 状态不正常代表包含敏感词
        if (!answer.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        return answer
    }

    @DeleteMapping
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        if (!user.hasPerm(Group.PERM_ANSWER_DELETE)) return Responses.forbidden()
        ids.forEach { service.deleteLogical(it) }
        return Responses.ok()
    }
}