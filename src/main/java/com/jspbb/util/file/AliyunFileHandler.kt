package com.jspbb.util.file

import com.aliyun.oss.OSSClient
import com.aliyun.oss.common.auth.DefaultCredentialProvider
import com.aliyun.oss.model.GetObjectRequest
import org.apache.commons.io.FilenameUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream


/**
 * https://help.aliyun.com/document_detail/32013.html
 */
class AliyunFileHandler(
        private val endpoint: String,
        private val accessKey: String,
        private val secretKey: String,
        private val region: String,
        private val bucket: String,
        private val storePrefix: String = "",
        private val displayPrefix: String = ""
) : FileHandler {
    override fun getDisplayPrefix(): String = displayPrefix
    override fun store(filename: String, part: MultipartFile) {
        execute { client ->
            part.inputStream.use { inputStream ->
                client.putObject(bucket, storePrefix + filename, inputStream)
            }
        }
    }

    override fun store(filename: String, file: File) {
        execute { client ->
            client.putObject(bucket, storePrefix + filename, file)
        }
    }

    override fun getFile(filename: String): File? {
        val ext = FilenameUtils.getExtension(filename)
        val file = FilesEx.getTempFile(ext)
        execute { client ->
            client.getObject(GetObjectRequest(bucket, storePrefix + filename), file);
        }
        return if (file.exists()) file else null
    }

    override fun getInputStream(filename: String): InputStream? {
        var inputStream: InputStream? = null
        execute { client ->
            val ossObject = client.getObject(bucket, storePrefix + filename)
            // 获取输入流
            inputStream = ossObject?.objectContent
        }
        return inputStream
    }

    override fun delete(filename: String) {
        execute { client ->
            client.deleteObject(bucket, storePrefix + filename);
        }
    }

    private fun execute(process: (client: OSSClient) -> Unit) {
        // 创建OSSClient实例
        val client = OSSClient(endpoint, DefaultCredentialProvider(accessKey, secretKey), null)
        try {
            process(client)
        } finally {
            client.shutdown()
        }
    }
}