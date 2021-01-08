package com.jspbb.core.web.api

import com.jspbb.core.Constants.API
import com.jspbb.core.domain.Configs
import com.jspbb.core.domain.Sms
import com.jspbb.core.domain.User
import com.jspbb.core.domain.UserExt
import com.jspbb.core.domain.config.ConfigSignUp.Companion.VERIFY_MODE_EMAIL
import com.jspbb.core.domain.config.ConfigSignUp.Companion.VERIFY_MODE_EMAIL_OR_MOBILE
import com.jspbb.core.domain.config.ConfigSignUp.Companion.VERIFY_MODE_MOBILE
import com.jspbb.core.domain.config.ConfigSignUp.Companion.VERIFY_MODE_PICTURE
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.SmsService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
import com.jspbb.util.captcha.CaptchaTokenService
import com.jspbb.util.web.Servlets
import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.web.subject.WebSubject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 用户注册 Controller
 */
@RestController("apiSignUpController")
@RequestMapping(API)
class SignUpController(
        private val userService: UserService,
        private val smsService: SmsService,
        private val captchaService: CaptchaTokenService,
        private val configService: ConfigService
) {
    data class SignUpParam(val username: String, val password: String, val captcha: String?, val captchaToken: String?,
                           val mobile: String?, val mobileMessage: String?, val mobileMessageId: Long?,
                           val email: String?, val emailMessage: String?, val emailMessageId: Long?)

    /**
     * 注册。注册后只返回用户状态，不会自动登录。
     *
     * @return 用户状态
     */
    @PostMapping("sign_up")
    fun signUp(@RequestBody params: SignUpParam, request: HttpServletRequest, response: HttpServletResponse): Any {
        val ip = Servlets.getRemoteAddr(request)
        val user = signUp(params, ip, request)
        return if (user !is User) user else user.status
    }

    /**
     * 注册后如果用户状态正常，会自动登录。只适用于 session-cookie 模式。
     *
     * @return 用户状态
     */
    @PostMapping("sign_up_and_sign_in")
    fun signUpAndSignIn(@RequestBody params: SignUpController.SignUpParam, request: HttpServletRequest, response: HttpServletResponse): Any {
        val ip = Servlets.getRemoteAddr(request)
        val user = signUp(params, ip, request)
        if (user !is User) return user
        // 用户状态正常则直接登录
        if (user.isNormal()) {
            val subject = SecurityUtils.getSubject()
            // 防止session fixation attack(会话固定攻击)，让旧session失效
            subject.getSession(false)?.let { subject.logout() }
            val builder = WebSubject.Builder(request, response)
            val principals = SimplePrincipalCollection(user.username, "sign_up_realm")
            builder.principals(principals).authenticated(true)
            ThreadContext.bind(builder.buildSubject())
            userService.loginSuccess(user.ext, ip)
        }
        return user.status
    }

    fun signUp(params: SignUpParam, ip: String, request: HttpServletRequest): Any {
        val configs = configService.selectConfigs()
        val user = User(username = params.username, ext = UserExt(rowPassword = params.password, ip = ip))
        if (configs.signUp.verifyMode == VERIFY_MODE_PICTURE && !captchaService.validateCaptcha(params.captchaToken, params.captcha)) return Responses.badRequest(request, "error.captchaIncorrect")
        if (configs.signUp.verifyMode in arrayOf(VERIFY_MODE_EMAIL_OR_MOBILE, VERIFY_MODE_EMAIL, VERIFY_MODE_MOBILE)) {
            when {
                configs.signUp.verifyMode in arrayOf(VERIFY_MODE_EMAIL_OR_MOBILE, VERIFY_MODE_MOBILE) && smsService.validateMobileMessage(params.mobileMessage, params.mobileMessageId, params.mobile, Sms.USAGE_SIGN_UP) ->
                    user.mobile = params.mobile
                configs.signUp.verifyMode in arrayOf(VERIFY_MODE_EMAIL_OR_MOBILE, VERIFY_MODE_EMAIL) && smsService.validateEmailMessage(params.emailMessage, params.emailMessageId, params.email, Sms.USAGE_SIGN_UP) ->
                    user.email = params.email
                else -> return Responses.badRequest("mobileMessage or emailMessage not valid")
            }
        }
        validateSignUp(params.username, user.mobile, user.email, configs, userService)?.let { return it }
        userService.insert(user, user.ext)
        return user
    }

    companion object {
        fun validateSignUp(username: String, mobile: String? = null, email: String? = null, configs: Configs, userService: UserService): ResponseEntity<Responses.Body>? {
            // 检查用户名是否合法
            if (username.length < configs.signUp.usernameMinLength) {
                return Responses.badRequest("username length ${username.length} less than min length ${configs.signUp.usernameMaxLength}")
            }
            if (username.length > configs.signUp.usernameMaxLength) {
                return Responses.badRequest("username length ${username.length} greater than max length ${configs.signUp.usernameMaxLength}")
            }
            if (!Pattern.compile(configs.signUp.usernameRegex).matcher(username).matches()) {
                return Responses.badRequest("username '$username' not matchers regex '${configs.signUp.usernameRegex}'")
            }
            // 检查用户名是否已经存在
            userService.selectByUsername(username)?.let { return Responses.badRequest("username '$username' already exists") }
            // 检查手机号码是否已经存在
            mobile?.let { userService.selectByMobile(mobile)?.let { return Responses.badRequest("mobile '$mobile' already exists") } }
            // 检查邮箱是否已经存在
            email?.let { userService.selectByEmail(email)?.let { return Responses.badRequest("email '$email' already exists") } }
            return null;
        }

    }
}
