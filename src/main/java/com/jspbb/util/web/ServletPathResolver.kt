package com.jspbb.util.web

import org.springframework.web.context.ServletContextAware
import java.io.File
import javax.servlet.ServletContext

/**
 * ServletContext PathResolver
 */
class ServletPathResolver : PathResolver, ServletContextAware {
    /**
     * @see PathResolver.getRealPath
     */
    override fun getRealPath(uri: String): String = servletContext.getRealPath(uri)

    /**
     * @see PathResolver.getRealPath
     */
    override fun getRealPath(uri: String, prefix: String?): String {
        if (prefix != null && prefix.startsWith("file:")) {
            val builder = StringBuilder()
            builder.append(prefix.substring(5)).append(uri.replace('/', File.separatorChar))
            return builder.toString()
        } else {
            return servletContext.getRealPath((prefix ?: "") + uri)
        }
    }

    /**
     * @see PathResolver.getContextPath
     */
    override fun getContextPath(): String = servletContext.contextPath

    private lateinit var servletContext: ServletContext
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }
}