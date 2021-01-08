package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Configs
import com.jspbb.core.domain.Sms.Companion.USAGE_CHANGE_EMAIL
import com.jspbb.core.domain.Sms.Companion.USAGE_CHANGE_MOBILE
import com.jspbb.core.domain.User
import com.jspbb.core.domain.UserExt
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.SmsService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Contexts
import com.jspbb.core.support.Responses
import com.jspbb.util.file.FileHandler
import com.jspbb.util.file.FilesEx
import com.jspbb.util.image.ImageHandler
import com.jspbb.util.image.Images
import com.jspbb.util.security.HashCredentialsDigest
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession


/**
 * Created by PONY on 2017/4/29.
 */
@RestController("apiSettingsController")
@RequestMapping("$API/settings")
class SettingsController(
        private val userService: UserService,
        private val smsService: SmsService,
        private val credentialsDigest: HashCredentialsDigest,
        private val imageHandler: ImageHandler,
        private val fileHandler: FileHandler,
        private val configService: ConfigService
) {
    data class ProfileParam(val title: String?, val location: String?, val gender: String?, val birthday: OffsetDateTime?)

    @PutMapping("profile")
    fun profile(@RequestBody params: ProfileParam, request: HttpServletRequest, modelMap: Model): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        user.ext.title = params.title
        user.ext.location = params.location
        if (params.gender != null) user.ext.gender = params.gender
        user.ext.birthday = params.birthday
        userService.update(user.ext)
        return Responses.ok()
    }

    data class PictureParam(val url: String, val x: Int, val y: Int, val width: Int, val height: Int)

    @PutMapping("picture")
    fun picture(@RequestBody params: PictureParam, request: HttpServletRequest, modelMap: Model): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        val src = fileHandler.getName(params.url) ?: return Responses.badRequest("file not found: ${params.url}")
        val file = fileHandler.getFile(src) ?: return Responses.badRequest("file not found: $src")
        val configs = configService.selectConfigs()
        return saveProfilePicture(user, file, params.x, params.y, params.width, params.height, configs, fileHandler, imageHandler, userService)
    }

    data class PasswordParam(val oldPassword: String?, val password: String)

    @PutMapping("password")
    fun password(@RequestBody params: PasswordParam, request: HttpServletRequest, modelMap: Model): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        if (user.ext.isHasPassword() && !credentialsDigest.matches(user.ext.password, params.oldPassword, user.ext.salt)) {
            return Responses.badRequest(request, "error.incorrectPassword");
        }
        userService.updatePassword(user.ext, params.password)
        return Responses.ok()
    }

    data class EmailParam(val email: String, val emailMessage: String, val emailMessageId: Long, val password: String?)

    @PutMapping("email")
    fun email(@RequestBody params: EmailParam, request: HttpServletRequest, session: HttpSession, modelMap: Model): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        if (user.ext.isHasPassword() && !credentialsDigest.matches(user.ext.password, params.password, user.ext.salt)) {
            return Responses.badRequest(request, "error.incorrectPassword");
        }
        // 验证邮箱地址是否重复
        if (userService.selectByEmail(params.email) != null) return Responses.badRequest(request, "error.emailExists");
        if (!smsService.validateEmailMessage(params.emailMessage, params.emailMessageId, params.email, USAGE_CHANGE_EMAIL)) return Responses.badRequest("emailMessage not valid")
        user.email = params.email
        userService.update(user)
        return Responses.ok()
    }

    data class MobileParam(val mobile: String, val mobileMessage: String, val mobileMessageId: Long, val password: String?)

    @PutMapping("mobile")
    fun mobile(@RequestBody params: MobileParam, request: HttpServletRequest, session: HttpSession, modelMap: Model): Any {
        val username = Contexts.getUsername() ?: return Responses.unauthorized()
        val user = userService.selectByUsername(username) ?: return Responses.unauthorized()
        if (user.ext.isHasPassword() && !credentialsDigest.matches(user.ext.password, params.password, user.ext.salt)) {
            return Responses.badRequest(request, "error.incorrectPassword");
        }
        // 验证手机地址是否重复
        if (userService.selectByEmail(params.mobile) != null) return Responses.badRequest(request, "error.mobileExists");
        if (!smsService.validateMobileMessage(params.mobileMessage, params.mobileMessageId, params.mobile, USAGE_CHANGE_MOBILE)) return Responses.badRequest("mobileMessage not valid")
        user.mobile = params.mobile
        userService.update(user)
        return Responses.ok()
    }

    companion object {
        /**
         * 保存头像
         */
        fun saveProfilePicture(user: User, file: File, x: Int?, y: Int?, width: Int?, height: Int?, configs: Configs, fileHandler: FileHandler, imageHandler: ImageHandler, userService: UserService): Any {
            var tempFile = file;
            try {
                val extension = Images.getFormatName(file) ?: return Responses.badRequest("Image format type not supported.")
                // 如果图片文件后缀与图片格式不一致，则新建一个后缀正确的文件
                if (FilenameUtils.getExtension(file.name) != extension) tempFile = file.copyTo(FilesEx.getTempFile(extension), true)
                // 删除原头像
                deleteOldProfilePicture(user.ext, fileHandler)
                // 保存新头像
                user.ext.pictureVersion++
                val name = user.ext.getPictureFilename(extension)
                val destUrl = fileHandler.getDisplayPrefix() + name
                user.ext.pictureUrl = destUrl
                // 保存原图
                val originalName = UserExt.getPictureUrlOriginal(name)
                fileHandler.store(originalName, tempFile)
                // 未裁剪成功，则直接拷贝原图
                if (x != null && y != null && width != null && height != null) {
                    imageHandler.crop(tempFile.absolutePath, tempFile.absolutePath, x, y, width, height)
                }
                // 生成大图
                val largeName = UserExt.getPictureUrlLarge(name)
                imageHandler.resize(tempFile.absolutePath, tempFile.absolutePath, configs.signUp.pictureSizeLarge, configs.signUp.pictureSizeLarge)
                fileHandler.store(largeName, tempFile)
                // 生成中图
                val mediumName = UserExt.getPictureUrlMedium(name)
                imageHandler.resize(tempFile.absolutePath, tempFile.absolutePath, configs.signUp.pictureSizeMedium, configs.signUp.pictureSizeMedium)
                fileHandler.store(mediumName, tempFile)
                // 生成小图
                imageHandler.resize(tempFile.absolutePath, tempFile.absolutePath, configs.signUp.pictureSizeSmall, configs.signUp.pictureSizeSmall)
                fileHandler.store(name, tempFile)
                // 更新头像设置
                userService.update(user.ext)
                return mapOf("fileUrl" to destUrl)
            } finally {
                if (file.exists()) FileUtils.deleteQuietly(file)
                if (tempFile.exists()) FileUtils.deleteQuietly(tempFile)
            }
        }

        /**
         * 删除旧头像
         */
        private fun deleteOldProfilePicture(ext: UserExt, fileHandler: FileHandler) {
            val name = fileHandler.getName(ext.pictureUrl)
            if (name != null) {
                val originalName = UserExt.getPictureUrlOriginal(name)
                fileHandler.delete(originalName)
                val largeName = UserExt.getPictureUrlLarge(name)
                fileHandler.delete(largeName)
                val mediumName = UserExt.getPictureUrlMedium(name)
                fileHandler.delete(mediumName)
                fileHandler.delete(name)
            }
        }
    }
}
