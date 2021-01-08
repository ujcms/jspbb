package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Answer
import com.jspbb.core.domain.AnswerExt
import com.jspbb.core.domain.Question
import com.jspbb.core.domain.QuestionExt
import com.jspbb.core.listener.CommentDeleteListener
import com.jspbb.core.listener.QuestionDeleteListener
import com.jspbb.core.mapper.AnswerExtMapper
import com.jspbb.core.mapper.AnswerMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime


/**
 * 回答Service
 */
@Service
class AnswerService(
        private val ipSeeker: IpSeeker,
        private val questionService: QuestionService,
        private val commentService: CommentService,
        private val attachService: AttachService,
        private val seqService: SeqService,
        private val extMapper: AnswerExtMapper,
        private val mapper: AnswerMapper
) : CommentDeleteListener, QuestionDeleteListener {

    fun select(id: Long): Answer? = mapper.select(id)

    @Transactional
    fun insert(answer: Answer, ext: AnswerExt) {
        answer.id = seqService.getNextVal(Answer.TABLE_NAME)
        ext.id = answer.id
        ext.markdownToHtml()
        val region = ipSeeker.find(ext.ip)
        ext.ipCountry = region.country
        ext.ipProvince = region.province
        ext.ipCity = region.city
        answer.ext = ext
        mapper.insert(answer)
        extMapper.insert(ext)
        // 正常状态的回答，修改问题的活跃类型。
        if (answer.isNormal()) {
            val question = questionService.select(answer.questionId) ?: throw RuntimeException("Question not found: ${answer.questionId}")
            question.answerCount++
            question.activeUserId = answer.userId
            question.activeDate = OffsetDateTime.now()
            question.ext.activeType = QuestionExt.ACTIVE_ANSWERED
            questionService.update(question, question.ext)
        }
        attachService.use(ext.text, Answer.TYPE_NAME, answer.id)
    }

    @Transactional
    fun update(record: Answer, ext: AnswerExt): Long {
        ext.markdownToHtml()
        extMapper.update(ext)
        return mapper.update(record)
    }

    @Transactional
    fun update(answer: Answer, ext: AnswerExt, markdown: String, ip: String, editUserId: Long): Long {
        val origText = ext.text
        answer.editUserId = editUserId
        answer.editDate = OffsetDateTime.now()
        ext.markdown = markdown
        ext.markdownToHtml()
        ext.editCount += 1

        val question = questionService.select(answer.questionId) ?: throw RuntimeException("Question not found: ${answer.questionId}")
        question.activeDate = OffsetDateTime.now()
        question.ext.activeType = QuestionExt.ACTIVE_EDITED
        questionService.update(answer.question, answer.question.ext)

        attachService.reuse(origText, ext.text, Answer.TYPE_NAME, answer.id)
        //TODO 插入历史记录
        extMapper.update(ext)
        return mapper.update(answer)
    }

    @Transactional
    fun deleteLogical(id: Long) {
        select(id)?.let { answer ->
            updateQuestionActive(answer)
            answer.status = Answer.STATUS_DELETED
            mapper.update(answer)
        }
    }

    @Transactional
    fun delete(id: Long): Long {
        select(id)?.let { answer ->
            updateQuestionActive(answer)
            attachService.disuse(answer.ext.text, Answer.TYPE_NAME, answer.id)
            extMapper.delete(id)
            return mapper.delete(id)
        }
        return 0
    }

    private fun updateQuestionActive(answer: Answer) {
        if (answer.isNormal()) {
            val question = answer.question
            question.answerCount -= 1
            // 更新活动状态，避免被删用户信息显示在页面上
            question.activeUserId = question.userId
            question.activeUser = question.user
            questionService.update(question)
        }
    }

    fun selectList(questionId: Long?): List<Answer> = mapper.selectList(questionId)

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Answer> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Answer> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Answer>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    override fun preCommentDelete(id: Long) {
        commentService.select(id)?.let { comment ->
            if (comment.refType == Answer.TYPE_NAME) {
                mapper.select(comment.refId)?.let { bean ->
                    if (bean.commentCount > 0) bean.commentCount--
                    mapper.update(bean)
                }
            }
        }
    }

    override fun preQuestionDelete(id: Long) = mapper.selectList(id).forEach { bean -> delete(bean.id) }
}
