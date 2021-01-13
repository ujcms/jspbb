package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import com.jspbb.util.web.HtmlHelper
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.time.OffsetDateTime

/**
 * 评论实体类
 */
open class Comment(
        var id: Long = Long.MIN_VALUE,
        /** 父ID */
        var parentId: Long? = null,
        /** 用户ID */
        var userId: Long = Long.MIN_VALUE,
        /** 修改用户ID */
        var editUserId: Long? = null,
        /** 评论对象类型 */
        var refType: String = "",
        /** 评论对象ID */
        var refId: Long = Long.MIN_VALUE,
        /** 创建日期 */
        var created: OffsetDateTime = OffsetDateTime.now(),
        /** 修改日期 */
        var editDate: OffsetDateTime? = null,
        /** 正文markdown */
        var markdown: String = "",
        /** 正文html */
        var text: String = "",
        /** 匹配到的敏感词 */
        var sensitiveWords: String? = null,
        /** 状态 */
        var status: Int = STATUS_NORMAL,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null,
        /** 创建者 */
        open var user: User = User(),
        /** 创建者 */
        open var editUser: User? = User()
) {
    /** markdown转成html */
    fun getHtml(): String {
        return HtmlHelper.cleanHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(markdown)))
    }

    fun markdownToHtml() {
        text = getHtml();
    }

    fun isNormal() = status == Answer.STATUS_NORMAL

    companion object {
        /** 评论表名 */
        const val TABLE_NAME = "jspbb_comment"

        /** 附件类型名 */
        const val ATTACH_TYPE = "comment"

        /** 通知类型：新的评论 */
        const val NOTIFICATION_TYPE = "comment"

        /** 通知类型：评论更新 */
        const val NOTIFICATION_UPDATE_TYPE = "commentUpdate"

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