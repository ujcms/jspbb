package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.*
import com.jspbb.core.domain.User.Companion.RESTRICT_LOGIN_ERROR
import com.jspbb.core.domain.User.Companion.RESTRICT_MESSAGE
import com.jspbb.core.domain.User.Companion.RESTRICT_UPLOAD
import com.jspbb.core.mapper.*
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.security.CredentialsDigest
import com.jspbb.util.security.Digests
import com.jspbb.util.web.Dates
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 通知Service
 */
@Service
class NotificationService(
        private val userService: UserService,
        private val seqService: SeqService,
        private val mapper: NotificationMapper
) {
    @Autowired
    private lateinit var messagingTemplate: SimpMessagingTemplate;

    fun select(id: Long): Notification? = mapper.select(id)

    @Transactional
    fun insert(record: Notification) {
        record.id = seqService.getNextVal(Notification.TABLE_NAME)
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Notification): Long {
        return mapper.update(record)
    }

    @Transactional
    fun delete(id: Long): Long {
        return mapper.delete(id)
    }

    @Transactional
    fun send(username: String, userId: Long, type: String, data: String, body: String, url: String): Notification {
        val record = Notification(userId = userId, type = type, data = data, body = if (body.length > 450) body.substring(0, 450) else body, url = url)
        insert(record)
        messagingTemplate.convertAndSendToUser(username, NOTIFICATION_DEST, record)
        return record
    }

    @Transactional
    fun deleteByTypeAndData(userId: Long, type: String, data: String): Long = mapper.deleteByTypeAndData(userId, type, data)

    fun countByUserId(userId: Long): Long = mapper.countByUserId(userId)


    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Notification> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Notification> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    companion object {
        /** 通知订阅地址 */
        const val NOTIFICATION_DEST = "/queue/notifications"
    }
}
