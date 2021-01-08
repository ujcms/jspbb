package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Sms
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.SmsService
import com.jspbb.core.support.Responses
import com.jspbb.util.captcha.CaptchaTokenService
import com.jspbb.util.web.Servlets
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

/**
 * Created by PONY on 2017.10.17.
 */
@RestController("apiSmsController")
@RequestMapping(API)
class SmsController(
        private val captchaService: CaptchaTokenService,
        private val configService: ConfigService,
        private val service: SmsService,
        @Value("\${aliyun.mns.verifyCodeTimeout}")
        private val verifyCodeTimeout: Int
) {
    data class MobileMessageParam(val mobile: String, val usage: String, val captcha: String, val captchaToken: String)

    @PostMapping("mobile_message")
    fun mobileMessage(@RequestBody params: MobileMessageParam, request: HttpServletRequest): Any {
        if (!captchaService.validateCaptcha(params.captchaToken, params.captcha)) return Responses.badRequest(request, "error.captchaIncorrect")
        val ip = Servlets.getRemoteAddr(request)
        val configs = configService.selectConfigs()
        if (configs.restrict.inMobileBlacklist(params.mobile)) return Responses.badRequest(request, "error.inMobileBlacklist")
        val beginDate = OffsetDateTime.now().minusMinutes(configs.restrict.smsWithin.toLong())
        if (service.countByIp(ip, beginDate, Sms.TYPE_MOBILE) > configs.restrict.smsMax) return Responses.badRequest("the number of mobile message over limit")
        val sms = service.sendMobileMessage(params.mobile, params.usage, ip)
        return sms.id
    }

    data class EmailMessageParam(val email: String, val usage: String, val captcha: String, val captchaToken: String)

    @PostMapping("email_message")
    fun emailMessage(@RequestBody params: EmailMessageParam, request: HttpServletRequest): Any {
        if (!captchaService.validateCaptcha(params.captchaToken, params.captcha)) return Responses.badRequest(request, "error.captchaIncorrect")
        val ip = Servlets.getRemoteAddr(request)
        val configs = configService.selectConfigs()
        val beginDate = OffsetDateTime.now().minusMinutes(configs.restrict.smsWithin.toLong())
        if (service.countByIp(ip, beginDate, Sms.TYPE_EMAIL) > configs.restrict.smsMax) return Responses.badRequest("the number of email message over limit")
        val sms = service.sendEmailMessage(params.email, params.usage, ip)
        return sms.id
    }

    @GetMapping("try_mobile_message")
    fun tryMobileMessage(mobileMessage: String?, mobileMessageId: Long?, mobile: String?): Any = service.tryMobileMessage(mobileMessage, mobileMessageId, mobile)

    @GetMapping("try_email_message")
    fun tryEmailMessage(emailMessage: String?, emailMessageId: Long?, email: String?): Any = service.tryEmailMessage(emailMessage, emailMessageId, email)
}