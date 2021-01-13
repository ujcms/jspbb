package com.jspbb.core.web.apicp

import com.jspbb.core.Constants.APICP
import com.jspbb.core.domain.config.*
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.SensitiveService
import com.jspbb.core.support.Responses
import org.apache.commons.lang3.StringUtils
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


/**
 * Created by PONY on 2017/4/29.
 */
@RestController
@RequestMapping("$APICP/config")
class ConfigController(
        private val thymeleafProperties: ThymeleafProperties,
        private val applicationContext: ApplicationContext,
        private val sensitiveService: SensitiveService,
        private val service: ConfigService
) {
    @GetMapping("/base")
    @RequiresPermissions("config:base:show")
    fun base(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.base
    }

    @PutMapping("/base")
    @RequiresPermissions("config:base:update")
    fun updateBase(@RequestBody base: ConfigBase, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigBase.TYPE_PREFIX, base.toMap())
        return base
    }

    @GetMapping("/template-dirs")
    @RequiresPermissions("config:base:show")
    fun templateDirs(request: HttpServletRequest): Array<String> {
        val path = applicationContext.getResource(thymeleafProperties.prefix)
        // error 目录（显示错误信息的模板模板）不显示
        return if (path.isFile) path.file.list { _, name -> name != "error" } ?: emptyArray() else emptyArray()
    }

    @GetMapping("/sign-up")
    @RequiresPermissions("config:signUp:show")
    fun signUp(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.signUp
    }

    @PutMapping("/sign-up")
    @RequiresPermissions("config:signUp:update")
    fun updateSignUp(@RequestBody signUp: ConfigSignUp, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigSignUp.TYPE_PREFIX, signUp.toMap())
        return signUp
    }

    @GetMapping("/email")
    @RequiresPermissions("config:email:show")
    fun email(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.email
    }

    @PutMapping("/email")
    @RequiresPermissions("config:email:update")
    fun updateEmail(@RequestBody email: ConfigEmail, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigEmail.TYPE_PREFIX, email.toMap())
        return email
    }

    /**
     * 保存并发送测试邮件
     */
    @PostMapping("/send-email")
    @RequiresPermissions("config:email:update")
    fun sendEmail(@RequestBody email: ConfigEmail, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigEmail.TYPE_PREFIX, email.toMap())
        email.sendMail(arrayOf(email.testTo), email.testSubject, email.testText)
        return email
    }

    @GetMapping("/restrict")
    @RequiresPermissions("config:restrict:show")
    fun restrict(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.restrict
    }

    @PutMapping("/restrict")
    @RequiresPermissions("config:restrict:update")
    fun updateRestrict(@RequestBody restrict: ConfigRestrict, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigRestrict.TYPE_PREFIX, restrict.toMap())
        return restrict
    }

    @GetMapping("/upload")
    @RequiresPermissions("config:upload:show")
    fun upload(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.upload
    }

    @PutMapping("/upload")
    @RequiresPermissions("config:upload:update")
    fun updateUpload(@RequestBody upload: ConfigUpload, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigUpload.TYPE_PREFIX, upload.toMap())
        return upload
    }

    @GetMapping("/watermark")
    @RequiresPermissions("config:watermark:show")
    fun watermark(request: HttpServletRequest): Any {
        val configs = service.selectConfigs()
        return configs.watermark
    }

    @PutMapping("/watermark")
    @RequiresPermissions("config:watermark:update")
    fun updateWatermark(@RequestBody watermark: ConfigWatermark, request: HttpServletRequest): Any {
        service.updateByNamePrefix(ConfigWatermark.TYPE_PREFIX, watermark.toMap())
        return watermark
    }

    @GetMapping("/sensitive-words")
    @RequiresPermissions("config:sensitive_words:show")
    fun sensitiveWord(): Any? {
        return sensitiveService.readSensitiveWords()
    }

    @PutMapping("/sensitive-words")
    @RequiresPermissions("config:sensitive_words:update")
    fun updateSensitiveWord(@RequestBody words: String, request: HttpServletRequest): Any? {
        sensitiveService.setSensitiveWords(StringUtils.split(words, "\r\n").toSet())
        sensitiveService.writeSensitiveWordList(words)
        return Responses.ok()
    }
}
