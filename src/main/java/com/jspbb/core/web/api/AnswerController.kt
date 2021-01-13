package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Answer
import com.jspbb.core.domain.AnswerExt
import com.jspbb.core.domain.Group
import com.jspbb.core.service.*
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController("apiAnswerController")
@RequestMapping("$API/answers")
class AnswerController(
        private val notificationService: NotificationService,
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
        // 非受信用户在禁止发帖时段
        if(user.inForbiddenTime()) return Responses.badRequest(request, "error.inForbiddenTime")
        // 是否发帖过快
        if (!user.isAnswerAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val answer = Answer(questionId = questionId, question = question, userId = user.id, user = user,
                ext = AnswerExt(markdown = params.markdown, ip = Servlets.getRemoteAddr(request)))
        // 包含敏感词，则屏蔽
        val hits = sensitiveService.finds(answer.ext.markdown)
        if (hits.isNotEmpty()) {
            answer.status = Answer.STATUS_SHIELDED
            // 记录第一个敏感词
            answer.ext.sensitiveWords = hits[0].toString()
        }
        service.insert(answer, answer.ext)
        // 记录发帖时间
        userService.addUpAnswer(user)
        // 状态不正常代表包含敏感词
        if (!answer.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        // 给提问人发通知，提问人不是本人才发
        if (question.userId != answer.userId) {
            // data要使用question的id，方便删除
            notificationService.send(question.user.username, question.user.id, Answer.NOTIFICATION_TYPE, question.id.toString(), "${answer.user.username}: ${answer.ext.markdown}", Answer.getUrl(question.id, answer.id))
        }
        // 给管理员组发通知
        userService.selectByGroupId(Group.ADMIN_GROUP_ID).forEach {
            // 回答者和提问者都不是本人，才发通知
            if (answer.userId != it.id && question.userId != it.id) {
                // data要使用question的id，方便删除
                notificationService.send(it.username, it.id, Answer.NOTIFICATION_TYPE, question.id.toString(), "${answer.user.username}: ${answer.ext.markdown}", Answer.getUrl(question.id, answer.id))
            }
        }
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
        val hits = sensitiveService.finds(answer.ext.markdown)
        if (hits.isNotEmpty()) {
            answer.status = Answer.STATUS_SHIELDED
            // 记录第一个敏感词
            answer.ext.sensitiveWords = hits[0].toString()
        }
        service.update(answer, answer.ext, params.markdown, Servlets.getRemoteAddr(request), user.id)
        // 状态不正常代表包含敏感词
        if (!answer.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        // 给提问人发通知，提问人不是本人才发
        val question = answer.question
        if (question.userId != answer.userId) {
            // data要使用question的id，方便删除
            notificationService.send(question.user.username, question.user.id, Answer.NOTIFICATION_UPDATE_TYPE, question.id.toString(), "${answer.user.username}: ${answer.ext.markdown}", Answer.getUrl(question.id, answer.id))
        }
        // 给管理员组发通知
        userService.selectByGroupId(Group.ADMIN_GROUP_ID).forEach {
            // 回答者和提问者都不是本人，才发通知
            if (answer.userId != it.id && question.userId != it.id) {
                // data要使用question的id，方便删除
                notificationService.send(it.username, it.id, Answer.NOTIFICATION_UPDATE_TYPE, question.id.toString(), "${answer.user.username}: ${answer.ext.markdown}", Answer.getUrl(question.id, answer.id))
            }
        }
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