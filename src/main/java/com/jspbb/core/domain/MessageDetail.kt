package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import com.jspbb.util.web.HtmlHelper
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup

/**
 * 消息详细实体类
 */
class MessageDetail(
        var id: Long = Long.MIN_VALUE,
        /** 标题 */
        var title: String? = null,
        /** 正文 */
        var markdown: String = "",
        /** 被引用数量。可以被发件箱和收件箱引用，如果引用数量为0，则代表这条消息已经不被使用，可以删除。 */
        var referredCount: Int = 2,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null
) {
    fun getHtml(): String {
        return HtmlHelper.cleanHtml(HtmlRenderer.builder().build().render(Parser.builder().build().parse(markdown)))
    }

    fun getText(): String {
        val doc = Jsoup.parse(getHtml())
        return doc.body().text()
    }

    companion object {
        /** 问题表名 */
        const val TABLE_NAME = "jspbb_message_detail"
    }
}