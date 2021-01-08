package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.MessageDetail
import com.jspbb.core.domain.Question
import com.jspbb.core.mapper.MessageDetailMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 用户Service
 */
@Service
class MessageDetailService(
        private val ipSeeker: IpSeeker,
        private val seqService: SeqService,
        private val mapper: MessageDetailMapper
) {
    fun select(id: Long): MessageDetail? = mapper.select(id)

    @Transactional
    fun insert(record: MessageDetail) {
        record.id = seqService.getNextVal(MessageDetail.TABLE_NAME)
        val region = ipSeeker.find(record.ip)
        record.ipCountry = region.country
        record.ipProvince = region.province
        record.ipCity = region.city
        mapper.insert(record)
    }

    @Transactional
    fun update(record: MessageDetail): Long {
        return mapper.update(record)
    }

    @Transactional
    fun delete(id: Long): Long {
        return mapper.delete(id)
    }

    @Transactional
    fun minusReferredCount(ids: Collection<Long>): Long {
        return mapper.minusReferredCount(ids)
    }

    @Transactional
    fun deleteOrphan(ids: Collection<Long>): Long {
        return mapper.deleteOrphan(ids)
    }

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<MessageDetail> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<MessageDetail> {
        val parser = QueryParser(queryMap, "id_desc")
        if (limit == null) {
            return mapper.selectAll(parser)
        } else {
            return PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }
}
