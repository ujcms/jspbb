package com.jspbb.util.web

/**
 * 真实路径Resolver
 */
interface PathResolver {
    /**
     * 通过uri获取真实路径
     */
    fun getRealPath(uri: String): String

    /**
     * 通过uri获取真实路径。[prefix] 支持file:格式开头。
     */
    fun getRealPath(uri: String, prefix: String?): String

    /**
     * 获取上下文路径
     */
    fun getContextPath(): String
}