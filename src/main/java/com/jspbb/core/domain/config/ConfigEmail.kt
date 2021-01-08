package com.jspbb.core.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.readValue
import com.jspbb.core.domain.Configs
import com.jspbb.core.domain.Configs.Companion.mapper
import com.jspbb.core.domain.Configs.Companion.toMap
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import java.util.*

/**
 * 邮箱设置类
 */
data class ConfigEmail(
        var host: String? = null,
        var port: Int? = null,
        var auth: Boolean = true,
        var ssl: Boolean = true,
        var timeout: Int? = null,
        var from: String = "username@email.com",
        var username: String = "username",
        var password: String = "password",
        var testTo: String = "test@email.com",
        var testSubject: String = "确认SMTP服务器配置",
        var testText: String = "如果您收到这封邮件，说明SMTP服务器配置正确。"
) {
    fun sendMail(to: Array<String>, subject: String, text: String) {
        if (host == null) {
            throw RuntimeException("email smtp host is not set")
        }
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.username = username
        mailSender.password = password
        mailSender.defaultEncoding = "UTF-8"
        mailSender.port = port ?: JavaMailSenderImpl.DEFAULT_PORT
        val prop = Properties()
        if (auth) prop["mail.smtp.auth"] = auth.toString()
        if (ssl) prop["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        if (timeout != null) prop["mail.smtp.timeout"] = timeout.toString()
        mailSender.javaMailProperties = prop
        val msg = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(msg, true, "UTF-8")
        helper.setFrom(from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text, false)
        mailSender.send(msg)
    }

    @JsonIgnore
    fun toMap() = Configs.toMap(this)

    companion object {
        const val TYPE_PREFIX = "email_"
    }
}