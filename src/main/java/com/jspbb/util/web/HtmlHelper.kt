package com.jspbb.util.web

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Cleaner
import org.jsoup.safety.Whitelist

/**
 * HTML帮助类
 */
object HtmlHelper {
    /**
     * 提取html中图片和链接的URL
     */
    fun getImageAndAttach(html: String?): List<String> {
        val urls = ArrayList<String>()
        if (html.isNullOrBlank()) return urls
        val doc = Jsoup.parseBodyFragment(html)
        doc.select("img,a").forEach { element ->
            when (element.tagName()) {
                "img" -> {
                    element.attr("src")?.let { urls.add(it) }
                }
                "a" -> {
                    element.attr("href")?.let { urls.add(it) }
                }
            }
        }
        return urls
    }

    /**
     * 清除html中的不安全代码
     */
    fun cleanHtml(html: String): String {
        val outputSettings = Document.OutputSettings().prettyPrint(false)
        val whitelist = Whitelist.relaxed().addAttributes("code", "class")
        val baseUri = "http://none.cn"

        val dirty = Jsoup.parseBodyFragment(html, baseUri)
        val cleaner = Cleaner(whitelist)
        val clean = cleaner.clean(dirty)
        clean.select("img,a").forEach { element ->
            when (element.tagName()) {
                "img" -> {
                    element.attr("src")?.let {
                        if (it.startsWith(baseUri)) element.attr("src", it.substring(baseUri.length))
                    }
                }
                "a" -> {
                    element.attr("href")?.let {
                        if (it.startsWith(baseUri)) element.attr("href", it.substring(baseUri.length))
                    }
                }
            }
        }
        clean.outputSettings(outputSettings)
        return clean.body().html()
    }
}