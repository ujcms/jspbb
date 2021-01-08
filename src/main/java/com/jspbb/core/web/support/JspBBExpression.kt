package com.jspbb.core.web.support

import com.jspbb.util.web.Dates
import com.jspbb.util.web.TimerInterceptor.TIMER_START
import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.spring5.context.SpringContextUtils
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Duration
import java.time.OffsetDateTime
import java.util.regex.Pattern

class JspBBExpression(private val context: IExpressionContext) {
    //    val locale = context.locale!!
    private val requestContext = SpringContextUtils.getRequestContext(context) ?: throw RuntimeException("Cannot get ThymeleafRequestContext")

    /**
     * 社交风格时间格式化
     */
    fun prettyTime(date: OffsetDateTime): String {
        val between = Duration.between(date, OffsetDateTime.now())
        val diff = between.toMillis()
        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour
        val week = 7 * day
        val month = 30 * day
        val year = 365 * day
        return when (diff) {
            in year..Long.MAX_VALUE -> Dates.formatDate(date)
            in month..year -> requestContext.getMessage("prettyTime.monthAgo", arrayOf(diff / month))
            in week..month -> requestContext.getMessage("prettyTime.weekAgo", arrayOf(diff / week))
            in day..week -> requestContext.getMessage("prettyTime.dayAgo", arrayOf(diff / day))
            in hour..day -> requestContext.getMessage("prettyTime.hourAgo", arrayOf(diff / hour))
            in minute..hour -> requestContext.getMessage("prettyTime.minuteAgo", arrayOf(diff / minute))
            else -> requestContext.getMessage("prettyTime.justNow")
        }
    }

    /**
     * Thymeleaf expression 不支持 kotlin 的默认参数，必须写多个
     */
    fun paging(page: Int = 1): String = paging(page, null)

    fun paging(page: Int = 1, anchor: String? = null): String = paging(page, anchor, context.getVariable("url") as String)
    /**
     * 获取分页url
     *
     * [page] 页码
     * [anchor] 锚点，如 #abc
     * [url] 当前url地址
     */
    fun paging(page: Int = 1, anchor: String? = null, url: String): String {
        val i = url.indexOf("?")
        val uri = if (i == -1) url else url.substring(0, i)
        var queryString = if (i == -1) "" else url.substring(i + 1)
        if (queryString.isNotBlank()) {
            // 删除原有page。page=3&page=4&page=10&page=0
            val p = Pattern.compile("""\&*\s*page\s*=[^\&]*""")
            val m = p.matcher(queryString)
            queryString = m.replaceAll("").trim()
            // 如果刚好删除的参数在第一个，如page=3&abc=123，有可能出现&abc=123，要删除第一个&
            if (queryString.startsWith("&")) {
                queryString = queryString.substring(1)
            }
        }
        var pagingUrl: String
        if (queryString.isNotBlank()) {
            pagingUrl = if (page > 1) {
                "$uri?$queryString&page=$page"
            } else {
                "$uri?$queryString"
            }
        } else {
            pagingUrl = if (page > 1) {
                "$uri?page=$page"
            } else {
                uri
            }
        }
        if (!anchor.isNullOrBlank()) {
            pagingUrl += anchor
        }
        return pagingUrl
    }

    fun processedIn(): String {
        val begin = context.getVariable(TIMER_START) as Long?
        return if (begin != null) {
            val end = System.currentTimeMillis()
            val processed = BigDecimal(end - begin).divide(BigDecimal(1000))
            "Processed in ${DecimalFormat("0.000").format(processed)} second(s)."
        } else {
            ""
        }
    }
}