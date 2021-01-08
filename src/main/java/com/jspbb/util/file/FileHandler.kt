package com.jspbb.util.file

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream

interface FileHandler {
    fun getName(url: String?): String? = if (url != null && url.startsWith(getDisplayPrefix())) url.substring(getDisplayPrefix().length) else null
    fun getDisplayPrefix(): String
    fun store(filename: String, part: MultipartFile)
    fun store(filename: String, file: File)
    fun getFile(filename: String): File?
    fun getInputStream(filename: String): InputStream?
    fun delete(filename: String)
}
