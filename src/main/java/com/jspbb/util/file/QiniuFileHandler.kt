package com.jspbb.util.file

import com.google.gson.Gson
import com.qiniu.common.Zone
import com.qiniu.storage.BucketManager
import com.qiniu.storage.Configuration
import com.qiniu.storage.UploadManager
import com.qiniu.storage.model.DefaultPutRet
import com.qiniu.util.Auth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


/**
 * https://developer.qiniu.com/kodo/sdk/1239/java#5
 */
class QiniuFileHandler(
        private val accessKey: String,
        private val secretKey: String,
        private val bucketName: String,
        private val bucketDomain: String,
        private val storePrefix: String = "",
        private val displayPrefix: String = ""
) : FileHandler {
    override fun getDisplayPrefix(): String = displayPrefix
    override fun store(filename: String, part: MultipartFile) {
        execute { uploadManager, uploadToken ->
            part.inputStream.use { inputString ->
                val response = uploadManager.put(inputString, storePrefix + filename, uploadToken, null, null)
                //解析上传成功的结果
                val putRet = Gson().fromJson(response.bodyString(), DefaultPutRet::class.java)
                logger.debug("response key: ${putRet.key}; response hash: ${putRet.hash}")
            }
        }
    }

    override fun store(filename: String, file: File) {
        execute { uploadManager, uploadToken ->
            val response = uploadManager.put(file, storePrefix + filename, uploadToken)
            //解析上传成功的结果
            val putRet = Gson().fromJson(response.bodyString(), DefaultPutRet::class.java)
            logger.debug("response key: ${putRet.key}; response hash: ${putRet.hash}")
        }
    }

    override fun getFile(filename: String): File? {
        val encodedFileName = URLEncoder.encode(storePrefix + filename, "utf-8");
        val finalUrl = String.format("%s/%s", bucketDomain, encodedFileName);
        return FilesEx.getFileFromUrl(URL(finalUrl), null)
    }

    override fun getInputStream(filename: String): InputStream? {
        val encodedFileName = URLEncoder.encode(storePrefix + filename, "utf-8");
        val finalUrl = String.format("%s/%s", bucketDomain, encodedFileName);
        val conn = URL(finalUrl).openConnection() as HttpURLConnection
        return if (conn.responseCode != 200) conn.inputStream else null
    }

    override fun delete(filename: String) {
        val cfg = Configuration(Zone.zone0())
        val auth = Auth.create(accessKey, secretKey)
        val bucketManager = BucketManager(auth, cfg)
        bucketManager.delete(bucketName, storePrefix + filename)
    }

    private fun execute(process: (uploadManager: UploadManager, uploadToken: String) -> Unit) {
        //构造一个带指定Zone对象的配置类
        val cfg = Configuration(Zone.autoZone())
        //...其他参数参考类注释
        val uploadManager = UploadManager(cfg)
        val auth = Auth.create(accessKey, secretKey)
        val uploadToken = auth.uploadToken(bucketName)
        process(uploadManager, uploadToken)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(QiniuFileHandler::class.java.name)
    }
}