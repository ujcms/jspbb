package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import com.jspbb.util.web.HtmlHelper
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * 问题扩展实体类
 */
open class QuestionExt(
        var id: Long = Long.MIN_VALUE,
        /** 标题 */
        var title: String = "",
        /** 正文markdown */
        var markdown: String? = null,
        /** 正文 */
        var text: String? = null,
        /** 匹配到的敏感词 */
        var sensitiveWords: String? = null,
        /** 活跃类型 */
        var activeType: String = ACTIVE_ASKED,
        /** 修改次数 */
        var editCount: Int = 0,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null
) {
    fun markdownToHtml() {
        markdown?.let {
            HtmlRenderer.builder().build().render(Parser.builder().build().parse(it))?.let { html ->
                // 清除html中的不安全代码
                text = HtmlHelper.cleanHtml(html)
            }
        }
    }

    companion object {
        const val ACTIVE_ASKED = "asked"
        const val ACTIVE_ANSWERED = "answered"
        const val ACTIVE_COMMENTED = "commented"
        const val ACTIVE_EDITED = "edited"
    }
}