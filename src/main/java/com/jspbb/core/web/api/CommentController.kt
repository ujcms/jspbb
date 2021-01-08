package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.*
import com.jspbb.core.service.*
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

/**
 * 评论Controller
 */
@RestController("apiCommentController")
@RequestMapping("$API/comments")
class CommentController(
        private val sensitiveService: SensitiveService,
        private val questionService: QuestionService,
        private val answerService: AnswerService,
        private val userService: UserService,
        private val service: CommentService
) {
    data class CommentParam(val id: Long?, val refId: Long, val parentId: Long?, val markdown: String);

    @PostMapping("question")
    fun createQuestionComment(@RequestBody params: CommentParam, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // 是否发帖过快
        if (!user.isCommentAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val bean = questionService.select(params.refId) ?: return Responses.badRequest("Question id not found: ${params.refId}")
        val result = create(user, Question.TYPE_NAME, params.refId, params.parentId, params.markdown, request)
        // 执行成功应返回Comment对象，返回 ResponseEntity 对象则代表执行失败
        if (result is Comment) {
            // 状态不正常代表包含敏感词
            if (!result.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
            updateQuestionActive(bean, user)
            // 更新评论数
            bean.commentCount++
            questionService.update(bean, bean.ext)
        }
        return result
    }

    @PostMapping("answer")
    fun createAnswerComment(@RequestBody params: CommentParam, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // 是否发帖过快
        if (!user.isCommentAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val bean = answerService.select(params.refId) ?: return Responses.badRequest("Answer id not found: ${params.refId}")
        val result = create(user, Answer.TYPE_NAME, params.refId, params.parentId, params.markdown, request)
        // 执行成功应返回Comment对象，返回 ResponseEntity 对象则代表执行失败
        if (result is Comment) {
            // 状态不正常代表包含敏感词
            if (!result.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
            updateQuestionActive(bean.question, user)
            // 更新评论数
            bean.commentCount++
            answerService.update(bean, bean.ext)
        }
        return result
    }

    @PutMapping
    fun update(@RequestBody params: CommentParam, request: HttpServletRequest): Any {
        val id = params.id ?: return Responses.badRequest("Comment id cannot be null")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val bean = service.select(id) ?: return Responses.badRequest("Comment not found: $id")
        // 自己和有权限的用户可以修改
        if (!user.hasPerm(Group.PERM_COMMENT_EDIT, bean.userId)) return Responses.forbidden()
        bean.markdown = params.markdown
        bean.editDate = OffsetDateTime.now()
        bean.editUserId = user.id
        bean.editUser = user
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(bean.markdown)) bean.status = Comment.STATUS_SHIELDED
        service.update(bean);
        // 状态不正常代表包含敏感词
        if (!bean.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
        return Responses.ok()
    }

    /** 更新问题活跃信息 */
    private fun updateQuestionActive(question: Question, user: User) {
        question.activeUserId = user.id
        question.activeUser = user
        question.activeDate = OffsetDateTime.now()
        question.ext.activeType = QuestionExt.ACTIVE_COMMENTED
        questionService.update(question, question.ext)
    }

    private fun create(user: User, refType: String, refId: Long, parentId: Long?, markdown: String, request: HttpServletRequest): Any {
        if (parentId != null) {
            val parent = service.select(parentId) ?: return Responses.badRequest("parent id not found: $parentId")
            if (parent.refType != refType || parent.refId != refId) return Responses.badRequest("refType or refId incorrect with parent id: $parentId")
        }
        // 是否发帖过快
        if (!user.isCommentAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val bean = Comment(refType = refType, refId = refId, parentId = parentId, markdown = markdown, ip = Servlets.getRemoteAddr(request), userId = user.id, user = user)
        // 包含敏感词，则屏蔽
        if (sensitiveService.matches(bean.markdown)) bean.status = Comment.STATUS_SHIELDED
        service.insert(bean)
        // 记录发帖时间
        userService.addUpComment(user)
        return bean
    }

    @DeleteMapping
    fun delete(@RequestBody ids: List<Long>, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        ids.forEach { id ->
            service.select(id)?.let { comment ->
                if (user.hasPerm(Group.PERM_COMMENT_DELETE, comment.userId)) {
                    if (comment.refType == Question.TYPE_NAME) {
                        questionService.select(comment.refId)?.let { question ->
                            question.activeUserId = question.userId
                            question.activeUser = question.user
                            questionService.update(question)
                        }
                    } else if (comment.refType == Answer.TYPE_NAME) {
                        answerService.select(comment.refId)?.let { answer ->
                            val question = answer.question
                            question.activeUserId = question.userId
                            question.activeUser = question.user
                            questionService.update(question)
                        }
                    }
                    service.deleteLogical(comment.id)
                }
            }
        }
        return Responses.ok()
    }
}