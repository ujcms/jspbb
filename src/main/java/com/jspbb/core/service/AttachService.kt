package com.jspbb.core.service

import com.jspbb.core.domain.Attach
import com.jspbb.core.domain.AttachRef
import com.jspbb.core.mapper.AttachMapper
import com.jspbb.core.mapper.AttachRefMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.HtmlHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 附件Service
 */
@Service
class AttachService(
        private val ipSeeker: IpSeeker,
        private val seqService: SeqService,
        private val refMapper: AttachRefMapper,
        private val mapper: AttachMapper
) {
    fun select(id: Long): Attach? = mapper.select(id)

    @Transactional
    fun insert(record: Attach) {
        record.id = seqService.getNextVal(Attach.TABLE_NAME)
        val region = ipSeeker.find(record.ip)
        record.ipCountry = region.country
        record.ipProvince = region.province
        record.ipCity = region.city
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Attach): Long {
        return mapper.update(record)
    }

    @Transactional
    fun delete(id: Long): Long {
        return mapper.delete(id)
    }


    /**
     * HTML使用附件
     */
    @Transactional
    fun use(html: String?, type: String, id: Long) {
        for (img in HtmlHelper.getImageAndAttach(html)) {
            mapper.selectByUrl(img)?.let {
                // 如果没有引用记录，则插入引用记录
                if (refMapper.select(it.id, type, id) == null) {
                    refMapper.insert(AttachRef(it.id, type, id))
                    it.used = true
                    mapper.update(it)
                }
            }
        }
    }

    /**
     * HTML删除附件
     */
    @Transactional
    fun disuse(html: String?, type: String, id: Long) {
        for (img in HtmlHelper.getImageAndAttach(html)) {
            mapper.selectByUrl(img)?.let {
                refMapper.delete(it.id, type, id)
                it.used = refMapper.countByAttachId(it.id) > 0
                mapper.update(it)
            }
        }
    }

    /**
     * HTML重新使用附件
     */
    @Transactional
    fun reuse(origHtml: String?, html: String?, type: String, id: Long) {
        disuse(origHtml, type, id)
        use(html, type, id)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AttachService::class.java)
    }
}
