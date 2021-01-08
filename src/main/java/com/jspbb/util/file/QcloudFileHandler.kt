package com.jspbb.util.file

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.model.StorageClass
import com.qcloud.cos.region.Region
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream


/**
 * https://cloud.tencent.com/document/product/436/14113
 */
class QcloudFileHandler(
        private val accessKey: String,
        private val secretKey: String,
        private val region: String,
        private val bucket: String,
        private val storePrefix: String = "",
        private val displayPrefix: String = ""
) : FileHandler {
    override fun getDisplayPrefix(): String = displayPrefix
    override fun store(filename: String, part: MultipartFile) {
        val objectMetadata = ObjectMetadata()
        // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
        objectMetadata.contentLength = part.size
        execute { client ->
            part.inputStream.use { inputStream ->
                val putObjectRequest = PutObjectRequest(bucket, storePrefix + filename, inputStream, objectMetadata)
                // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
                putObjectRequest.setStorageClass(StorageClass.Standard_IA)
                val putObjectResult = client.putObject(putObjectRequest)
                // putObjectResult会返回文件的etag
                logger.debug(putObjectResult.eTag)
            }
        }
    }

    override fun store(filename: String, file: File) {
        execute { client ->
            val putObjectRequest = PutObjectRequest(bucket, storePrefix + filename, file)
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA)
            val putObjectResult = client.putObject(putObjectRequest)
            // putObjectResult会返回文件的etag
            logger.debug(putObjectResult.eTag)
        }
    }

    override fun getFile(filename: String): File? {
        val ext = FilenameUtils.getExtension(filename)
        val file = FilesEx.getTempFile(ext)
        execute { client ->
            client.getObject(bucket, storePrefix + filename)?.objectContent.use { inputString ->
                file.outputStream().use { outputStream ->
                    IOUtils.copyLarge(inputString, outputStream)
                }
            }
        }
        return if (file.exists()) file else null
    }

    override fun getInputStream(filename: String): InputStream? {
        var inputStream: InputStream? = null
        execute { client ->
            // 下载文件
            val cosObject = client.getObject(bucket, storePrefix + filename)
            // 获取输入流
            inputStream = cosObject?.objectContent
        }
        return inputStream
    }

    override fun delete(filename: String) {
        execute { client ->
            client.deleteObject(bucket, storePrefix + filename);
        }
    }

    private fun execute(process: (client: COSClient) -> Unit) {
        // 1 初始化用户身份信息(secretId, secretKey)
        val cred = BasicCOSCredentials(accessKey, secretKey)
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        val clientConfig = ClientConfig(Region(region))
        // 3 生成cos客户端
        val client = COSClient(cred, clientConfig)
        try {
            process(client)
        } finally {
            client.shutdown()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(QcloudFileHandler::class.java.name)
    }
}