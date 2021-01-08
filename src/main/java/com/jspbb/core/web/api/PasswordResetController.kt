package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Sms.Companion.USAGE_RESET_PASSWORD
import com.jspbb.core.service.SmsService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@RestController("apiPasswordResetController")
@RequestMapping("$API/password_reset")
class PasswordResetController(
        private val smsService: SmsService,
        private val userService: UserService
) {
    data class PasswordParam(val email: String?, val emailMessage: String?, val emailMessageId: Long?, val mobile: String?, val mobileMessage: String?, val mobileMessageId: Long?, val password: String)

    @PostMapping
    fun passwordReset(@RequestBody params: PasswordParam, request: HttpServletRequest, session: HttpSession): Any {
        when {
            params.email != null -> {
                if (!smsService.validateEmailMessage(params.emailMessage, params.emailMessageId, params.email, USAGE_RESET_PASSWORD)) return Responses.badRequest("emailMessage not valid")
                val user = userService.selectByEmail(params.email) ?: return Responses.badRequest("email not found: ${params.email}");
                userService.updatePassword(user.ext, params.password)
                return Responses.ok()
            }
            params.mobile != null -> {
                if (!smsService.validateMobileMessage(params.mobileMessage, params.mobileMessageId, params.mobile, USAGE_RESET_PASSWORD)) return Responses.badRequest("mobileMessage not valid")
                val user = userService.selectByMobile(params.mobile) ?: return Responses.badRequest("mobile not found: ${params.mobile}");
                userService.updatePassword(user.ext, params.password)
                return Responses.ok()
            }
            else -> return Responses.badRequest("email or mobile cannot be null")
        }
    }
}