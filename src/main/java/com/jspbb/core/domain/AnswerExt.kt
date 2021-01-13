package com.jspbb.core.domain

import com.jspbb.core.Constants.IP_NONE
import com.jspbb.util.web.HtmlHelper
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * 回答扩展实体表
 */
open class AnswerExt(
        var id: Long = Long.MIN_VALUE,
        /** 正文markdwon */
        var markdown: String = "",
        /** 正文html */
        var text: String = "",
        /** 匹配到的敏感词 */
        var sensitiveWords: String? = null,
        /** IP */
        var ip: String = IP_NONE,
        /** IP国家 */
        var ipCountry: String? = null,
        /** IP省份 */
        var ipProvince: String? = null,
        /** IP城市 */
        var ipCity: String? = null,
        /** 修改次数 */
        var editCount: Int = 0
) {
    fun markdownToHtml() {
        // markdown转成html
        text = HtmlRenderer.builder().build().render(Parser.builder().build().parse(markdown))
        // 清除html中的不安全代码
        text = HtmlHelper.cleanHtml(text)
    }

}