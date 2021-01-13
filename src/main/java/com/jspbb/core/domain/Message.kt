package com.jspbb.core.domain

import org.springframework.web.util.UriTemplate
import java.time.OffsetDateTime

/**
 * 消息实体类
 */
open class Message(
        var id: Long = Long.MIN_VALUE,
        /** 所属用户 ID */
        var userId: Long = Long.MIN_VALUE,
        /** 联系人用户 ID */
        var contactUserId: Long = Long.MIN_VALUE,
        /** 发件人ID */
        var fromUserId: Long = Long.MIN_VALUE,
        /** 收件人ID */
        var toUserId: Long = Long.MIN_VALUE,
        /** 消息ID */
        var messageDetailId: Long = Long.MIN_VALUE,
        /** 收件日期 */
        var date: OffsetDateTime = OffsetDateTime.now(),
        /** 是否系统消息 */
        var sys: Boolean = false,
        /** 消息类型 (1:收件; 2:发件) */
        var type: Int = 1,
        /** （收件）是否未读 */
        var unread: Boolean = true,
        /** （收件，临时属性）按发件人分组统计的总共条数。 */
        var total: Int = 0,
        /** （收件，临时属性）按发件人分组统计的未读条数。 */
        var unreadCount: Int = 0,
        /** （发件）发件总数 */
        var sendTotal: Int = 1,
        /** （发件）阅读数量 */
        var readCount: Int = 0,
        /** 收件人 */
        open var user: User = User(),
        /** 联系人 */
        open var contactUser: User = User(),
        /** 发件人 */
        open var fromUser: User = User(),
        /** 收件人 */
        open var toUser: User = User(),
        /** 消息 */
        open var detail: MessageDetail = MessageDetail()
) {
    companion object {
        /** 消息表名 */
        const val TABLE_NAME = "jspbb_message"
        /** 通知类型 */
        const val NOTIFICATION_TYPE = "message"
        /** 类型：收件箱 */
        const val TYPE_INBOX = 1
        /** 类型：发件箱 */
        const val TYPE_OUTBOX = 2
        /** URL地址 */
        const val MESSAGES_CONTACT_URL = "/messages/contact/{contactUserId}"

        fun getMessageContactUrl(contactUserId: Long): String = UriTemplate(MESSAGES_CONTACT_URL).expand(contactUserId).toString()

    }
}