package com.jspbb.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * 用户组实体表
 */
open class Group(
        var id: Long = Long.MIN_VALUE,
        /** 名称 */
        var name: String = "",
        /** 描述 */
        var description: String? = null,
        /** 类型(0:预设,1:特殊,2:常规(自动升级)) */
        var type: Int = TYPE_NORMAL,
        /** 所需声望 */
        var reputation: Int = 0,
        /** 提问最大条数 */
        var questionMax: Int = 2,
        /** 提问限制间隔(分钟) */
        var questionWithin: Int = 240,
        /** 回答最大条数 */
        var answerMax: Int = 3,
        /** 回答限制间隔(分钟) */
        var answerWithin: Int = 240,
        /** 评论最大条数 */
        var commentMax: Int = 8,
        /** 评论限制间隔(分钟) */
        var commentWithin: Int = 240,
        /** 消息最大条数 */
        var messageMax: Int = 20,
        /** 消息限制间隔(分钟) */
        var messageWithin: Int = 240,
        /** 上传最大长度(MB) */
        var uploadMax: Int = 10,
        /** 上传限制间隔(分钟) */
        var uploadWithin: Int = 1440,
        /** 排序值 */
        var order: Int = 999999,
        /** 权限 */
        var perms: String? = null,
        /** 是否可信用户(投票可加声誉) */
        var isTrusted: Boolean = true) {
    /** 上传每用户最大尺寸（Byte） */
    fun getUploadMaxByte(): Long = uploadMax * 1024L * 1024L

    /** 是否有某个权限 */
    @JsonIgnore
    fun hasPerm(perm: String): Boolean = perms?.split(";")?.any { it == perm } ?: false

    companion object {
        /** 默认用户组 */
        const val DEFAULT_GROUP_ID = 10L

        /** 类型：预设 */
        const val TYPE_PREDEFINED = 0

        /** 类型：特殊 */
        const val TYPE_SPECIAL = 1

        /** 类型：常规(自动升级) */
        const val TYPE_NORMAL = 2

        const val PERM_QUESTION_EDIT = "questionEdit"
        const val PERM_QUESTION_DELETE = "questionDelete"
        const val PERM_ANSWER_EDIT = "answerEdit"
        const val PERM_ANSWER_DELETE = "answerDelete"
        const val PERM_COMMENT_EDIT = "commentEdit"
        const val PERM_COMMENT_DELETE = "commentDelete"

        /** 通知表名 */
        const val TABLE_NAME = "jspbb_group"
    }
}