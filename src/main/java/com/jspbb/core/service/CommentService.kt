package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Answer
import com.jspbb.core.domain.Comment
import com.jspbb.core.domain.Question
import com.jspbb.core.listener.CommentDeleteListener
import com.jspbb.core.mapper.CommentMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * 回答Service
 */
@Service
class CommentService(
        private val ipSeeker: IpSeeker,
        private val attachService: AttachService,
        private val seqService: SeqService,
        private val mapper: CommentMapper
) {
    @Autowired(required = false)
    @Lazy
    private var deleteListeners: List<CommentDeleteListener>? = null

    fun select(id: Long): Comment? = mapper.select(id)

    @Transactional
    fun insert(record: Comment) {
        record.id = seqService.getNextVal(Answer.TABLE_NAME)
        record.markdownToHtml()
        val region = ipSeeker.find(record.ip)
        record.ipCountry = region.country
        record.ipProvince = region.province
        record.ipCity = region.city
        mapper.insert(record)
        attachService.use(record.text, Comment.ATTACH_TYPE, record.id)
    }

    @Transactional
    fun update(record: Comment): Long {
        record.markdownToHtml()
        return mapper.update(record)
    }

    @Transactional
    fun deleteLogical(id: Long) {
        select(id)?.let { comment ->
            comment.status = Comment.STATUS_DELETED
            mapper.update(comment)
        }
    }

    @Transactional
    fun delete(id: Long): Long {
        // 将对自己的评论改为对自己上级的评论
        select(id)?.let { comment ->
            firePreDeleteDeleteListeners(id)
            mapper.updateParentId(comment.id, comment.parentId)
            mapper.delete(id)
        }
        return 0
    }

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Comment> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Comment> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) mapper.selectAll(parser)
        else PageHelper.offsetPage<Answer>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }

    }

    private fun firePreDeleteDeleteListeners(id: Long) {
        deleteListeners?.forEach {
            it.preCommentDelete(id)
        }
    }
}
