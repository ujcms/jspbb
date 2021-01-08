package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Attach
import com.jspbb.core.domain.config.ConfigUpload
import com.jspbb.core.service.AttachService
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.file.FileHandler
import com.jspbb.util.file.FilesEx
import com.jspbb.util.image.ImageHandler
import com.jspbb.util.image.Images
import com.jspbb.util.web.Servlets
import com.jspbb.util.web.Uploads
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.io.File
import javax.servlet.http.HttpServletRequest

/**
 * 上传Controller
 */
@RestController("apiUploadController")
@RequestMapping(API)
class UploadController(
        private val userService: UserService,
        private val attachService: AttachService,
        private val configService: ConfigService,
        private val imageHandler: ImageHandler,
        private val fileHandler: FileHandler
) {
    @PostMapping("upload_image")
    fun uploadImage(maxWidth: Int?, maxHeight: Int?, watermark: Boolean = true, thumbnailWidth: Int?, thumbnailHeight: Int?, request: HttpServletRequest): Any {
        if (request !is MultipartHttpServletRequest) return Responses.badRequest("not multipart request")
        val part = request.fileMap.values.first() ?: return Responses.badRequest("upload file not found")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val configs = configService.selectConfigs()
        // 是否上传过多
        if (!user.isUploadAllowed(part.size)) return Responses.badRequest(request, "error.uploadTooMuch")
        // 检查图片大小
        if (configs.upload.imageLimit != 0 && part.size > configs.upload.imageLimit * 1024) return Responses.badRequest("image size too large: ${part.size}. must less than ${configs.upload.imageLimit * 1024}")
        // 检查图片后缀
        var extension = FilenameUtils.getExtension(part.originalFilename).toLowerCase()
        if (!ConfigUpload.isValidType(configs.upload.imageTypes, extension)) return Responses.badRequest("image extension not allowed: '$extension'. allowed extension: '${configs.upload.imageTypes}'")
        val tempFile = FilesEx.getTempFile(extension)
        val thumbnailFile: File = FilesEx.getTempFile(extension);
        try {
            part.transferTo(tempFile)
            // 使用图片格式作为后缀。有时候支持透明格式的图片使用了错误的后缀(如png图片使用jpg后缀)，会导致透明部分变成黑色。
            extension = Images.getFormatName(tempFile) ?: return Responses.badRequest("image format type not supported.")
            // 获得存储路径和显示路径
            val name = "/image" + Uploads.getFilename(extension)
            val url = fileHandler.getDisplayPrefix() + name
            // 图片压缩。全局设置大于一定像素的图片进行压缩。
            val imageMaxWidth = maxWidth ?: configs.upload.imageMaxWidth
            val imageMaxHeight = maxHeight ?: configs.upload.imageMaxHeight
            if (imageMaxWidth > 0 || imageMaxHeight > 0) {
                imageHandler.resize(tempFile.absolutePath, tempFile.absolutePath, imageMaxWidth, imageMaxHeight)
            }
            // 图片水印。全局设置大于多少的图片加水印。可传参控制。
            if (configs.watermark.enabled && watermark) {
                imageHandler.watermark(tempFile.absolutePath, tempFile.absolutePath, configs.watermark.overlay, configs.watermark.position, configs.watermark.dissolve, configs.watermark.minWidth, configs.watermark.minHeight)
            }
            // 缩略图。图集需要缩略图，其他一般不需要。
            if (thumbnailWidth != null && thumbnailHeight != null) {
                imageHandler.resize(tempFile.absolutePath, thumbnailFile.absolutePath, thumbnailWidth, thumbnailHeight)
            }
            fileHandler.store(name, tempFile)
            if (thumbnailFile.exists()) {
                fileHandler.store(Uploads.getThumbnailName(name), thumbnailFile)
            }
            attachService.insert(Attach(userId = user.id, name = name, url = url, origName = part.originalFilename, length = part.size, ip = Servlets.getRemoteAddr(request)))
            userService.addUpUpload(user, part.size)
            return mapOf("fileUrl" to url)
        } finally {
            if (tempFile.exists()) FileUtils.deleteQuietly(tempFile)
            if (thumbnailFile.exists()) FileUtils.deleteQuietly(thumbnailFile)
        }
    }

    @PostMapping("upload_file")
    fun uploadFile(request: HttpServletRequest): Any {
        if (request !is MultipartHttpServletRequest) return Responses.badRequest("not multipart request")
        val part = request.fileMap.values.first() ?: return Responses.badRequest("upload file not found")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val configs = configService.selectConfigs()
        // 是否上传过多
        if (!user.isUploadAllowed(part.size)) return Responses.badRequest(request, "error.uploadTooMuch")
        // 检查文件后缀
        val extension = FilenameUtils.getExtension(part.originalFilename).toLowerCase()
        if (!ConfigUpload.isValidType(configs.upload.fileTypes, extension)) return Responses.badRequest("file extension not allowed: '$extension'. allowed extension: '${configs.upload.fileTypes}'")
        // 检查文件大小
        if (configs.upload.fileLimit != 0 && part.size > configs.upload.fileLimit * 1024) return Responses.badRequest("file size too large: ${part.size}. must less than ${configs.upload.fileLimit * 1024}")
        // 获得存储路径和显示路径
        val name = "/file" + Uploads.getFilename(extension)
        val url = fileHandler.getDisplayPrefix() + name
        val tempFile = FilesEx.getTempFile(extension)
        try {
            part.transferTo(tempFile)
            fileHandler.store(name, tempFile)
            attachService.insert(Attach(userId = user.id, name = name, url = url, origName = part.originalFilename, length = part.size, ip = Servlets.getRemoteAddr(request)))
            userService.addUpUpload(user, part.size)
            return mapOf("fileUrl" to url)
        } finally {
            if (tempFile.exists()) FileUtils.deleteQuietly(tempFile)
        }
    }

    @PostMapping("upload_video")
    fun uploadVideo(request: HttpServletRequest): Any {
        if (request !is MultipartHttpServletRequest) return Responses.badRequest("not multipart request")
        val part = request.fileMap.values.first() ?: return Responses.badRequest("upload file not found")
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val configs = configService.selectConfigs()
        // 是否上传过多
        if (!user.isUploadAllowed(part.size)) return Responses.badRequest(request, "error.uploadTooMuch")
        // 检查视频后缀
        val extension = FilenameUtils.getExtension(part.originalFilename).toLowerCase()
        if (!ConfigUpload.isValidType(configs.upload.videoTypes, extension)) return Responses.badRequest("video extension not allowed: '$extension'. allowed extension: '${configs.upload.videoTypes}'")
        // 检查视频大小
        if (configs.upload.videoLimit != 0 && part.size > configs.upload.videoLimit * 1024) return Responses.badRequest("video size too large: ${part.size}. must less than ${configs.upload.videoLimit * 1024}")
        // 获得存储路径和显示路径
        val name = "/video" + Uploads.getFilename(extension)
        val url = fileHandler.getDisplayPrefix() + name
        val tempFile = FilesEx.getTempFile(extension)
        try {
            part.transferTo(tempFile)
            fileHandler.store(name, tempFile)
            attachService.insert(Attach(userId = user.id, name = name, url = url, origName = part.originalFilename, length = part.size, ip = Servlets.getRemoteAddr(request)))
            userService.addUpUpload(user, part.size)
            return mapOf("fileUrl" to url)
        } finally {
            if (tempFile.exists()) FileUtils.deleteQuietly(tempFile)
        }
    }

    data class CropParam(val url: String, val x: Int, val y: Int, val width: Int, val height: Int, val maxWidth: Int, val maxHeight: Int)

    @PostMapping("crop_image")
    fun cropImage(params: CropParam, request: HttpServletRequest): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        // 是否上传过多
        if (!user.isUploadAllowed(0)) return Responses.badRequest(request, "error.uploadTooMuch")
        val src = fileHandler.getName(params.url) ?: return Responses.badRequest("external url not support: ${params.url}")
        // 图片裁剪。图片任意裁剪，生成新图片。
        val extension = FilenameUtils.getExtension(src)
        val name = "/image" + Uploads.getFilename(extension)
        val destUrl = fileHandler.getDisplayPrefix() + name
        val file = fileHandler.getFile(src) ?: return Responses.badRequest("file not found: $src")
        val tempFile = FilesEx.getTempFile(extension)
        try {
            // 未裁剪成功，则直接拷贝原图
            if (!imageHandler.crop(file.absolutePath, tempFile.absolutePath, params.x, params.y, params.width, params.height)) FileUtils.copyFile(file, tempFile)
            imageHandler.resize(tempFile.absolutePath, tempFile.absolutePath, params.maxWidth, params.maxHeight)
            fileHandler.store(name, tempFile)
            attachService.insert(Attach(userId = user.id, name = name, url = destUrl, origName = src, length = tempFile.length(), ip = Servlets.getRemoteAddr(request)))
            userService.addUpUpload(user, tempFile.length())
        } finally {
            if (file.exists()) FileUtils.deleteQuietly(file)
            if (tempFile.exists()) FileUtils.deleteQuietly(tempFile)
        }
        return mapOf("fileUrl" to destUrl)
    }
}