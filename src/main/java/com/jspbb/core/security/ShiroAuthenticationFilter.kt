package com.jspbb.core.security

import com.jspbb.core.Constants
import com.jspbb.core.Constants.CP
import com.jspbb.core.Constants.FALLBACK_URL_PARAM
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.UserService
import com.jspbb.util.captcha.CaptchaTokenService
import com.jspbb.util.captcha.CaptchaUtils
import com.jspbb.util.security.IncorrectCaptchaException
import com.jspbb.util.web.Servlets
import com.octo.captcha.service.CaptchaService
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.subject.Subject
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Shiro认证过滤器
 */
class ShiroAuthenticationFilter() : FormAuthenticationFilter() {
    override fun executeLogin(request: ServletRequest, response: ServletResponse): Boolean {
        request as HttpServletRequest; response as HttpServletResponse
        val token = createToken(request, response) as UsernamePasswordToken
        val user = userService.selectByUsername(token.username)
        // 判断是否需要验证码
        if (isSessionCaptchaRequired(request) || (user != null && user.isCaptchaRequired())) {
            val captcha = request.getParameter(CAPTCHA_PARAM)
            val captchaToken = request.getParameter(CAPTCHA_TOKEN_PARAM)
            if (captcha == null || !captchaService.validateCaptcha(captchaToken, captcha)) {
                return onLoginFailure(token, IncorrectCaptchaException(), request, response)
            }
        }
        return try {
            val subject = getSubject(request, response)
            // 防止session fixation attack(会话固定攻击)，让旧session失效
            // 先取出 Session 数据
            val errorCount = request.session.getAttribute(CAPTCHA_ERROR_COUNT_KEY) as Long?
            val savedRequest = request.session.getAttribute(WebUtils.SAVED_REQUEST_KEY) as SavedRequest?
            subject.session.stop()
            subject.getSession(true)
            // 将数据放回 Session
            errorCount?.let { request.session.setAttribute(CAPTCHA_ERROR_COUNT_KEY, it) }
            savedRequest?.let { request.session.setAttribute(WebUtils.SAVED_REQUEST_KEY, it) }
            subject.login(token)
            onLoginSuccess(token, subject, request, response)
        } catch (e: AuthenticationException) {
            // 如果由其它异常导致的，直接抛出，以免丢失异常信息导致难以发现问题
            e.cause?.let { throw it }
            onLoginFailure(token, e, request, response)
        }
    }

    override fun onLoginSuccess(token: AuthenticationToken?, subject: Subject?, request: ServletRequest?, response: ServletResponse?): Boolean {
        token as UsernamePasswordToken; request as HttpServletRequest; response as HttpServletResponse
        // 重置用户登录错误次数
        val user = userService.selectByUsername(token.username)
        if (user != null) {
            userService.loginSuccess(user.ext, Servlets.getRemoteAddr(request))
        }
        // 重置Session登录错误次数
        setErrorCount(request, 0)
        // 移除Session中验证码
        request.session.removeAttribute(CAPTCHA_REQUIRED_KEY)
        //TODO 记录登录成功日志
        return super.onLoginSuccess(token, subject, request, response)
    }

    override fun onLoginFailure(token: AuthenticationToken?, e: AuthenticationException?, request: ServletRequest?, response: ServletResponse?): Boolean {
        token as UsernamePasswordToken; request as HttpServletRequest; response as HttpServletResponse
        var errorCount = getErrorCount(request) + 1
        // 增加用户登录错误次数
        val user = userService.selectByUsername(token.username)
        if (user != null) {
            userService.loginFailure(user)
            userService.update(user, user.ext)
            // 如果用户登录错误次数已达到，则Session错误次数也要更新
            if (user.isCaptchaRequired()) errorCount = user.getLoginErrorCount()
        }
        // 增加Session登录错误次数
        setErrorCount(request, errorCount)
        // 在Session里设置是否需要验证码
        request.session.setAttribute(CAPTCHA_REQUIRED_KEY, isSessionCaptchaRequired(request))
        //TODO 记录登录失败日志
        return super.onLoginFailure(token, e, request, response)
    }

    @Throws(Exception::class)
    override fun issueSuccessRedirect(request: ServletRequest, response: ServletResponse) {
        request as HttpServletRequest; response as HttpServletResponse
        var fallbackUrl = request.getParameter(FALLBACK_URL_PARAM)
        if (fallbackUrl?.isNotBlank() == true) {
            WebUtils.issueRedirect(request, response, fallbackUrl, null, false)
        } else {
            // 根据前、后台地址判断返回的地址
            fallbackUrl = if (request.requestURI == CP || request.requestURI.startsWith(request.contextPath + CP + "/")) {
                Constants.CP
            } else {
                successUrl
            }
            WebUtils.redirectToSavedRequest(request, response, fallbackUrl)
        }
    }

    private fun isSessionCaptchaRequired(request: HttpServletRequest): Boolean = getErrorCount(request) >= configService.selectConfigs().restrict.passwordRetryMax
    private fun getErrorCount(request: HttpServletRequest) = request.session.getAttribute(CAPTCHA_ERROR_COUNT_KEY) as Long? ?: 0
    private fun setErrorCount(request: HttpServletRequest, errorCount: Long) = request.session.setAttribute(CAPTCHA_ERROR_COUNT_KEY, errorCount)

    @Autowired
    private lateinit var captchaService: CaptchaTokenService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var configService: ConfigService

    companion object {
        /** 验证码名称 */
        const val CAPTCHA_PARAM = "captcha"

        /** 验证码名称 */
        const val CAPTCHA_TOKEN_PARAM = "captchaToken"

        /** 验证码错误次数 */
        const val CAPTCHA_ERROR_COUNT_KEY = "shiroCaptchaErrorCount"

        /** 是否需要验证码 */
        const val CAPTCHA_REQUIRED_KEY = "shiroCaptchaRequired";
    }
}