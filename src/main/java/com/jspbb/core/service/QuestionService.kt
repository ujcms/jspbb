package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Question
import com.jspbb.core.domain.QuestionExt
import com.jspbb.core.listener.CommentDeleteListener
import com.jspbb.core.listener.QuestionDeleteListener
import com.jspbb.core.mapper.QuestionExtMapper
import com.jspbb.core.mapper.QuestionMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime


/**
 * 问题Service
 */
@Service
class QuestionService(
        private val ipSeeker: IpSeeker,
        private val attachService: AttachService,
        private val commentService: CommentService,
        private val seqService: SeqService,
        private val extMapper: QuestionExtMapper,
        private val mapper: QuestionMapper
) : CommentDeleteListener {
    @Lazy
    @Autowired(required = false)
    private var deleteListeners: List<QuestionDeleteListener>? = null

//    @Autowired
//    @Lazy
//    private lateinit var commentService: CommentService

    fun select(id: Long): Question? = mapper.select(id)

    @Transactional
    fun insert(record: Question, ext: QuestionExt) {
        record.id = seqService.getNextVal(Question.TABLE_NAME)
        ext.id = record.id
        ext.markdownToHtml()
        val region = ipSeeker.find(ext.ip)
        ext.ipCountry = region.country
        ext.ipProvince = region.province
        ext.ipCity = region.city
        record.ext = ext
        mapper.insert(record)
        extMapper.insert(ext)
        attachService.use(ext.text, Question.TYPE_NAME, record.id)
    }

    @Transactional
    fun update(record: Question, ext: QuestionExt): Long {
        ext.markdownToHtml()
        extMapper.update(ext)
        return mapper.update(record)
    }

    @Transactional
    fun updateExt(ext: QuestionExt): Long {
        ext.markdownToHtml()
        return extMapper.update(ext)
    }

    @Transactional
    fun update(record: Question): Long {
        return mapper.update(record)
    }

    @Transactional
    fun update(question: Question, ext: QuestionExt, title: String, markdown: String?, ip: String, editUserId: Long): Long {
        val origText = ext.text
        question.editUserId = editUserId
        question.editDate = OffsetDateTime.now()
        question.activeDate = question.editDate!!
        question.activeUserId = editUserId
        ext.title = title
        ext.markdown = markdown
        ext.activeType = QuestionExt.ACTIVE_EDITED
        ext.markdownToHtml()
        ext.editCount += 1
        attachService.reuse(origText, ext.text, Question.TYPE_NAME, question.id)
        //TODO 插入历史记录
        extMapper.update(ext)
        return mapper.update(question)
    }

    @Transactional
    fun deleteLogical(id: Long) {
        select(id)?.let { question ->
            question.status = Question.STATUS_DELETED
            mapper.update(question)
        }
    }

    @Transactional
    fun delete(id: Long): Long {
        //TODO 记录操作日志
        return select(id)?.let { question ->
            firePreDeleteDeleteListeners(id)
//            answerService.selectList(id).forEach { answer ->
//                answerService.delete(answer.id)
//            }
            attachService.disuse(question.ext.text, Question.TYPE_NAME, question.id)
            extMapper.delete(id)
            mapper.delete(id)
        } ?: 0
    }


    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Question> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Question> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    private fun firePreDeleteDeleteListeners(id: Long) {
        deleteListeners?.forEach {
            it.preQuestionDelete(id)
        }
    }

    override fun preCommentDelete(id: Long) {
        commentService.select(id)?.let { comment ->
            if (comment.refType == Question.TYPE_NAME) {
                mapper.select(comment.refId)?.let { bean ->
                    bean.commentCount--
                    mapper.update(bean)
                }
            }
        }
    }
}
