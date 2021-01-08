package com.jspbb.core.service

import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.*
import com.jspbb.core.mapper.MessageMapper
import com.jspbb.util.web.QueryParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 用户Service
 */
@Service
class MessageService(
        private val notificationService: NotificationService,
        private val detailService: MessageDetailService,
        private val seqService: SeqService,
        private val mapper: MessageMapper
) {
    fun select(id: Long): Message? = mapper.select(id)

    @Transactional
    fun insert(record: Message) {
        record.id = seqService.getNextVal(Message.TABLE_NAME)
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Message): Long {
        return mapper.update(record)
    }

    @Transactional
    fun delete(id: Long): Long {
        val message = select(id) ?: return 0
        mapper.delete(id)
        val detailIds = listOf<Long>(message.messageDetailId)
        detailService.minusReferredCount(detailIds)
        detailService.deleteOrphan(detailIds)
        return 1
    }

    /**
     * 发送短消息。分别一条保存发件Message和一条收件Message。
     *
     * @return 收件Message
     */
    @Transactional
    fun send(fromUserId: Long, toUserId: Long, toUsername: String, title: String?, markdown: String, ip: String): Message {
        val detail = MessageDetail(title = title, markdown = markdown, ip = ip)
        detailService.insert(detail)
        val outbox = Message(userId = fromUserId, contactUserId = toUserId, fromUserId = fromUserId, toUserId = toUserId, messageDetailId = detail.id,
                type = Message.TYPE_OUTBOX, unread = false, detail = detail)
        insert(outbox)
        val inbox = Message(userId = toUserId, contactUserId = fromUserId, fromUserId = fromUserId, toUserId = toUserId, messageDetailId = detail.id,
                type = Message.TYPE_INBOX, unread = true, detail = detail)
        insert(inbox)
        notificationService.send(toUsername, toUserId, Message.NOTIFICATION_TYPE, fromUserId.toString(), "$toUsername: ${inbox.detail.getText()}", Message.getMessageContactUrl(fromUserId))
        return outbox
    }

    @Transactional
    fun readByContactUserId(userId: Long, contactUserId: Long) {
        mapper.readByContactUserId(userId, contactUserId)
        notificationService.deleteByTypeAndData(userId, Message.NOTIFICATION_TYPE, contactUserId.toString())
    }

    @Transactional
    fun deleteByFromUserId(userId: Long, contactUserId: Long): Int {
        val messages = mapper.selectByContactUserId(userId, contactUserId)
        val size = messages.size
        if (size > 0) {
            val messageIds = messages.map { it.id }
            val detailIds = messages.map { it.messageDetailId }
            mapper.deleteByIds(messageIds)
            detailService.minusReferredCount(detailIds)
            detailService.deleteOrphan(detailIds)
        }
        return size
    }

    fun countUnread(userId: Long) = mapper.countUnread(userId)

    fun selectContactUserIdPage(userId: Long, contactUserId: Long, page: Int, pageSize: Int): Page<Message> {
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectByContactUserId(userId, contactUserId) }
    }

    fun selectContactUserIdList(userId: Long, contactUserId: Long, offset: Int = 0, limit: Int? = null): List<Message> {
        return if (limit == null) {
            mapper.selectByContactUserId(userId, contactUserId)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.selectByContactUserId(userId, contactUserId) }
        }
    }

    fun groupByUserIdPage(userId: Long, page: Int, pageSize: Int): Page<Message> {
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.groupByUserId(userId) }
    }

    fun groupByUserIdList(userId: Long, offset: Int = 0, limit: Int? = null): List<Message> {
        return if (limit == null) {
            mapper.groupByUserId(userId)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.groupByUserId(userId) }
        }
    }

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Message> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Question>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Message> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Question>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    @Autowired
    private lateinit var messageService: MessageDetailService;
}
