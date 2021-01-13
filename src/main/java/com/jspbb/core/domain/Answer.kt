package com.jspbb.core.domain

import org.springframework.web.util.UriTemplate
import java.time.OffsetDateTime

/**
 * 回答实体类
 */
open class Answer(
        var id: Long = Long.MIN_VALUE,
        /** 问题ID */
        var questionId: Long = Long.MIN_VALUE,
        /** 创建者ID */
        var userId: Long = Long.MIN_VALUE,
        /** 修改者ID */
        var editUserId: Long? = null,
        /** 创建日期 */
        var created: OffsetDateTime = OffsetDateTime.now(),
        /** 修改日期 */
        var editDate: OffsetDateTime? = null,
        /** 接受日期 */
        var acceptDate: OffsetDateTime? = null,
        /** 状态 */
        var status: Int = STATUS_NORMAL,
        /** 顶次数 */
        var ups: Int = 0,
        /** 踩次数 */
        var downs: Int = 0,
        /** 收藏次数 */
        var favoriteCount: Int = 0,
        /** 评论次数 */
        var commentCount: Int = 0,
        /** 是否采纳 */
        var accepted: Boolean = false,
        /** 创建者 */
        open var user: User = User(),
        /** 修改者 */
        open var editUser: User? = null,
        /** 问题 */
        open var question: Question = Question(),
        /** 答案扩展类 */
        open var ext: AnswerExt = AnswerExt()
) {
    fun isNormal() = status == STATUS_NORMAL

    companion object {
        /** 回答表名 */
        const val TABLE_NAME = "jspbb_answer"

        /** 类型名称。如附件类型、评论类型 */
        const val TYPE_NAME = "answer"

        /** 通知类型：新的回答 */
        const val NOTIFICATION_TYPE = "answer"
        /** 通知类型：回答更新 */
        const val NOTIFICATION_UPDATE_TYPE = "answerUpdate"

        /** 状态：正常 */
        const val STATUS_NORMAL = 0

        /** 状态：待审 */
        const val STATUS_PENDING = 1

        /** 状态：屏蔽 */
        const val STATUS_SHIELDED = 2

        /** 状态：删除 */
        const val STATUS_DELETED = 3

        /** 答案URL地址 */
        const val ANSWER_URL = "/questions/{id}/answers/{answerId}"

        fun getUrl(id: Long, answerId: Long): String = UriTemplate(ANSWER_URL).expand(id, answerId).toString()
    }
}