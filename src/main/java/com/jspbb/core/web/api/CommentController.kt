package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.*
import com.jspbb.core.service.*
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.web.Servlets
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

/**
 * 评论Controller
 */
@RestController("apiCommentController")
@RequestMapping("$API/comments")
class CommentController(
        private val notificationService: NotificationService,
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
        val question = questionService.select(params.refId) ?: return Responses.badRequest("Question id not found: ${params.refId}")
        val result = create(user, Question.TYPE_NAME, params.refId, params.parentId, params.markdown, request)
        // 执行成功应返回Comment对象，返回 ResponseEntity 对象则代表执行失败
        if (result is Comment) {
            // 状态不正常代表包含敏感词
            if (!result.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
            updateQuestionActive(question, user)
            // 更新评论数
            question.commentCount++
            questionService.update(question, question.ext)
            // 给提问人发通知。提问人不是本人，才发通知
            if (question.userId != result.userId) {
                // data要使用question的id，方便删除
                notificationService.send(question.user.username, question.user.id, Comment.NOTIFICATION_TYPE, question.id.toString(), "${result.user.username}: ${result.markdown}", Question.getUrl(question.id))
            }
            // 给管理员组发通知
            userService.selectByGroupId(Group.ADMIN_GROUP_ID).forEach {
                // 提问人和评论人都不是本来，才发通知
                if (question.userId != it.id && result.userId != it.id) {
                    // data要使用question的id，方便删除
                    notificationService.send(it.username, it.id, Comment.NOTIFICATION_TYPE, question.id.toString(), "${result.user.username}: ${result.markdown}", Question.getUrl(question.id))
                }
            }
        }
        return result
    }

    @PostMapping("answer")
    fun createAnswerComment(@RequestBody params: CommentParam, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // 是否发帖过快
        if (!user.isCommentAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val answer = answerService.select(params.refId) ?: return Responses.badRequest("Answer id not found: ${params.refId}")
        val result = create(user, Answer.TYPE_NAME, params.refId, params.parentId, params.markdown, request)
        // 执行成功应返回Comment对象，返回 ResponseEntity 对象则代表执行失败
        if (result is Comment) {
            // 状态不正常代表包含敏感词
            if (!result.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")
            updateQuestionActive(answer.question, user)
            // 更新评论数
            answer.commentCount++
            answerService.update(answer, answer.ext)

            val question = answer.question
            // 给回答人发通知。回答人不是本人，才发通知
            if (answer.userId != result.userId) {
                // data要使用question的id，方便删除
                notificationService.send(answer.user.username, answer.user.id, Comment.NOTIFICATION_TYPE, question.id.toString(), "${result.user.username}: ${result.markdown}", Answer.getUrl(question.id, answer.id))
            }
            // 给管理员组发通知
            userService.selectByGroupId(Group.ADMIN_GROUP_ID).forEach {
                // 回答人和评论人都不是本来，才发通知
                if (answer.userId != it.id && result.userId != it.id) {
                    // data要使用question的id，方便删除
                    notificationService.send(it.username, it.id, Comment.NOTIFICATION_TYPE, question.id.toString(), "${result.user.username}: ${result.markdown}", Answer.getUrl(question.id, answer.id))
                }
            }
        }
        return result
    }

    @PutMapping
    fun update(@RequestBody params: CommentParam, request: HttpServletRequest): Any {
        val id = params.id ?: return Responses.badRequest("Comment id cannot be null")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val comment = service.select(id) ?: return Responses.badRequest("Comment not found: $id")
        // 自己和有权限的用户可以修改
        if (!user.hasPerm(Group.PERM_COMMENT_EDIT, comment.userId)) return Responses.forbidden()
        comment.markdown = params.markdown
        comment.editDate = OffsetDateTime.now()
        comment.editUserId = user.id
        comment.editUser = user
        // 包含敏感词，则屏蔽
        val hits = sensitiveService.finds(comment.markdown)
        if (hits.isNotEmpty()) {
            comment.status = Comment.STATUS_SHIELDED
            // 记录第一个敏感词
            comment.sensitiveWords = hits[0].toString()
        }
        service.update(comment);
        // 状态不正常代表包含敏感词
        if (!comment.isNormal()) return Responses.badRequest(request, "error.containsSensitiveWords")

        val dataId: String?
        val receiverName: String?
        val receiverId: Long?
        val url: String?
        if (comment.refType == Question.TYPE_NAME) {
            val question = questionService.select(comment.refId) ?: return Responses.badRequest("Comment Type ${comment.refType} not found: $id")
            dataId = question.id.toString()
            receiverName = question.user.username
            receiverId = question.userId
            url = Question.getUrl(question.id)
        } else if (comment.refType == Answer.TYPE_NAME) {
            val answer = answerService.select(comment.refId) ?: return Responses.badRequest("Comment Type ${comment.refType} not found: $id")
            dataId = answer.question.id.toString()
            receiverName = answer.user.username
            receiverId = answer.userId
            url = Answer.getUrl(answer.questionId, answer.id)
        } else {
            dataId = null
            receiverName = null
            receiverId = null
            url = null
        }
        if (dataId != null && receiverName != null && receiverId != null && url != null) {
            // 发通知。接收人不是本人，才发通知
            if (receiverId != comment.userId) {
                // data要使用question的id，方便删除
                notificationService.send(receiverName, receiverId, Comment.NOTIFICATION_UPDATE_TYPE, dataId, "${comment.user.username}: ${comment.markdown}", url)
            }
            // 给管理员组发通知
            userService.selectByGroupId(Group.ADMIN_GROUP_ID).forEach {
                // 接收人和评论人都不是本来，才发通知
                if (receiverId != it.id && comment.userId != it.id) {
                    // data要使用question的id，方便删除
                    notificationService.send(it.username, it.id, Comment.NOTIFICATION_UPDATE_TYPE, dataId, "${comment.user.username}: ${comment.markdown}", url)
                }
            }
        }
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
        // 非受信用户在禁止发帖时段
        if (user.inForbiddenTime()) return Responses.badRequest(request, "error.inForbiddenTime")
        // 是否发帖过快
        if (!user.isCommentAllowed()) return Responses.badRequest(request, "error.postTooFast")
        val bean = Comment(refType = refType, refId = refId, parentId = parentId, markdown = markdown, ip = Servlets.getRemoteAddr(request), userId = user.id, user = user)
        // 包含敏感词，则屏蔽
        val hits = sensitiveService.finds(bean.markdown)
        if (hits.isNotEmpty()) {
            bean.status = Comment.STATUS_SHIELDED
            // 记录第一个敏感词
            bean.sensitiveWords = hits[0].toString()
        }
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