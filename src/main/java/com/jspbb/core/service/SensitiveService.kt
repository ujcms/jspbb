package com.jspbb.core.service

import com.jspbb.util.sensitive.Hit
import com.jspbb.util.sensitive.SensitiveFilter
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

/**
 * 敏感词 Service
 *
 * @author PONY
 */
@Service
class SensitiveService(
        private val resourceLoader: ResourceLoader,
        private val props: SensitiveProperties
) : InitializingBean {
    @Component
    @ConfigurationProperties(prefix = "sensitive")
    data class SensitiveProperties(var sensitiveWordsLocation: String = "")

    /** 敏感词过滤器 */
    private val sensitiveFilter = SensitiveFilter();
    private var sensitiveWordsFile: File? = null

    fun matches(text: String?): Boolean = sensitiveFilter.finds(text, false).size > 0

    fun finds(text: String?): MutableList<Hit> = sensitiveFilter.finds(text, false)

    fun filter(text: String?): String? = sensitiveFilter.filter(text)


    fun setSensitiveWords(lines: Collection<String>) = sensitiveFilter.setSensitiveWords(lines)

    fun readSensitiveWords() = sensitiveWordsFile?.let { FileUtils.readFileToString(it, UTF_8) } ?: ""

    fun writeSensitiveWordList(text: String) = sensitiveWordsFile?.let { FileUtils.write(it, text, UTF_8.name()) }

    override fun afterPropertiesSet() {
        val sensitiveWordsResource = resourceLoader.getResource(props.sensitiveWordsLocation)
        if (sensitiveWordsResource.isFile) sensitiveWordsFile = sensitiveWordsResource.file
        sensitiveFilter.setSensitiveWords(IOUtils.readLines(sensitiveWordsResource.inputStream, UTF_8))
    }

}
