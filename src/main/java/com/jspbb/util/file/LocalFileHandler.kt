package com.jspbb.util.file

import com.jspbb.util.web.PathResolver
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream

class LocalFileHandler(
        private val pathResolver: PathResolver,
        /** 保存路径前缀。默认为Servlet根路径(如:/uploads)，使用file:则为服务器绝对路径(如:file:c:/tomcat/uploads) */
        private val storePrefix: String = "/uploads",
        /** 显示路径前缀 */
        private val displayPrefix: String = "/uploads"
) : FileHandler {
    override fun getDisplayPrefix() = if (displayPrefix.startsWith("file:")) displayPrefix else pathResolver.getContextPath() + displayPrefix
    override fun store(filename: String, part: MultipartFile) {
        val dest = File(pathResolver.getRealPath(filename, storePrefix))
        if (!dest.parentFile.exists()) dest.parentFile.mkdirs()
        part.transferTo(dest)
    }

    override fun store(filename: String, file: File) {
        val dest = File(pathResolver.getRealPath(filename, storePrefix))
        // 原图片不能删除
        FileUtils.copyFile(file, dest)
    }

    override fun getFile(filename: String): File? {
        val ext = FilenameUtils.getExtension(filename)
        val tempFile = FilesEx.getTempFile(ext)
        val file = File(pathResolver.getRealPath(filename, storePrefix))
        // 返回临时文件
        if (file.exists()) FileUtils.copyFile(file, tempFile)
        return if (tempFile.exists()) tempFile else null
    }

    override fun getInputStream(filename: String): InputStream? {
        val file = File(storePrefix + filename)
        return if (file.exists()) file.inputStream() else null

    }

    override fun delete(filename: String) {
        FileUtils.deleteQuietly(File(pathResolver.getRealPath(filename, storePrefix)))
    }
}