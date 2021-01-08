package com.jspbb.util.file

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.net.ftp.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class FtpFileHandler(
        private val hostname: String,
        private val username: String,
        private val password: String,
        /** 是否使用 FTPS 协议（显式TLS） */
        private val useFTPS: Boolean = false,
        /** 默认使用 UTF-8 编码 */
        private val encoding: String? = "UTF-8",
        /** 留空为默认端口 */
        private val port: Int? = null,
        /** 是否主动模式 */
        private val localActive: Boolean = true,
        /** FTP远程路径前缀 */
        private val storePrefix: String = "",
        /** 显示地址前缀 */
        private val displayPrefix: String = ""
) : FileHandler {
    override fun getDisplayPrefix(): String = displayPrefix

    override fun store(filename: String, part: MultipartFile) {
        execute { client ->
            val pathname = storePrefix + filename
            val path = FilenameUtils.getFullPath(pathname)
            mkdir(client, path)
            part.inputStream.use {
                client.storeFile(pathname, it)
            }
        }
    }

    override fun store(filename: String, file: File) {
        execute { client ->
            val pathname = storePrefix + filename
            val path = FilenameUtils.getFullPath(pathname)
            mkdir(client, path)
            FileInputStream(file).use {
                client.storeFile(pathname, it)
            }
            FileUtils.deleteQuietly(file)
        }
    }

    override fun getFile(filename: String): File? {
        val ext = FilenameUtils.getExtension(filename)
        val file = FilesEx.getTempFile(ext)
        execute { client ->
            client.retrieveFileStream(storePrefix + filename)?.use { inputString ->
                file.outputStream().use { outputStream ->
                    IOUtils.copyLarge(inputString, outputStream)
                }
            }
        }
        return (if (file.exists()) file else null)
    }

    override fun getInputStream(filename: String): InputStream? {
        var inputStream: InputStream? = null
        execute { client ->
            inputStream = client.retrieveFileStream(storePrefix + filename)
        }
        return inputStream
    }

    override fun delete(filename: String) {
        execute { client ->
            val pathname = storePrefix + filename
            if (client.changeWorkingDirectory(pathname)) {
                delete(client, pathname)
            } else {
                client.deleteFile(pathname)
            }
        }
    }

    private fun delete(client: FTPClient, dir: String) {
        for (ftpFile in client.listFiles(dir)) {
            val pathname = dir + "/" + ftpFile.name
            if (ftpFile.isDirectory) {
                delete(client, pathname)
                client.removeDirectory(pathname)
            } else {
                client.deleteFile(pathname)
            }
        }
    }

    private fun mkdir(client: FTPClient, path: String) {
        if (path.startsWith("/")) {
            client.changeWorkingDirectory("/")
        }
        for (dir in path.split("/")) {
            if (!client.changeWorkingDirectory(dir)) {
                client.makeDirectory(dir)
                client.changeWorkingDirectory(dir)
            }
        }
    }

    fun execute(process: (client: FTPClient) -> Unit) {
        val client = if (useFTPS) FTPSClient() else FTPClient()
        if (encoding != null) client.controlEncoding = encoding
        val config = FTPClientConfig()
        client.configure(config)
        try {
            if (port != null) client.connect(hostname, port) else client.connect(hostname)
            val reply = client.replyCode
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect()
                throw RuntimeException("FTP server refused connection.")
            }
            if (!client.login(username, password)) {
                client.logout()
                throw RuntimeException("FTP login failure.")
            }
            client.setFileType(FTP.BINARY_FILE_TYPE)
            if (localActive) client.enterLocalActiveMode() else client.enterLocalPassiveMode()
            client.bufferSize = 1024 * 4
            process(client)
        } finally {
            if (client.isConnected) {
                client.disconnect()
            }
        }
    }
}