package com.jspbb.core.domain

import java.time.OffsetDateTime

/**
 * 问题实体类
 */
open class Question(
        var id: Long = Long.MIN_VALUE,
        /** 创建者ID */
        var userId: Long = Long.MIN_VALUE,
        /** 修改者ID */
        var editUserId: Long? = null,
        /** 活跃者ID */
        var activeUserId: Long = Long.MIN_VALUE,
        /** 创建日期 */
        var created: OffsetDateTime = OffsetDateTime.now(),
        /** 修改日期 */
        var editDate: OffsetDateTime? = null,
        /** 活跃日期 */
        var activeDate: OffsetDateTime = created,
        /** 状态 */
        var status: Int = STATUS_NORMAL,
        /** 顶次数 */
        var ups: Int = 0,
        /** 踩次数 */
        var downs: Int = 0,
        /** 回答次数 */
        var answerCount: Int = 0,
        /** 收藏次数 */
        var favoriteCount: Int = 0,
        /** 评论次数 */
        var commentCount: Int = 0,
        /** 浏览次数 */
        var views: Long = 0,
        /** 创建者 */
        open var user: User = User(),
        /** 修改者 */
        open var editUser: User? = null,
        /** 活跃者 */
        open var activeUser: User = User(),
        /** 问题扩展类 */
        open var ext: QuestionExt = QuestionExt()
) {

    fun isNormal() = status == Answer.STATUS_NORMAL

    companion object {
        /** 问题表名 */
        const val TABLE_NAME = "jspbb_question"

        /** 类型名称。如附件类型、评论类型 */
        const val TYPE_NAME = "question"
        /** 状态：正常 */
        const val STATUS_NORMAL = 0
        /** 状态：待审 */
        const val STATUS_PENDING = 1
        /** 状态：屏蔽 */
        const val STATUS_SHIELDED = 2
        /** 状态：删除 */
        const val STATUS_DELETED = 3
    }
}