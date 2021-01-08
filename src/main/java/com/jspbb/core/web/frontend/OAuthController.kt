package com.jspbb.core.web.frontend

import com.jspbb.core.Constants.FALLBACK_URL_PARAM
import com.jspbb.core.domain.Sms
import com.jspbb.core.domain.User
import com.jspbb.core.domain.UserExt
import com.jspbb.core.domain.UserOpenid
import com.jspbb.core.domain.config.ConfigSignUp.Companion.VERIFY_MODE_MOBILE
import com.jspbb.core.security.oauth.OAuthClient
import com.jspbb.core.security.oauth.OAuthToken
import com.jspbb.core.service.ConfigService
import com.jspbb.core.service.SmsService
import com.jspbb.core.service.UserService
import com.jspbb.core.support.Responses
import com.jspbb.core.web.api.SettingsController
import com.jspbb.util.captcha.CaptchaTokenService
import com.jspbb.util.file.FileHandler
import com.jspbb.util.file.FilesEx
import com.jspbb.util.image.ImageHandler
import com.jspbb.util.security.CredentialsDigest
import com.jspbb.util.security.Digests
import com.jspbb.util.web.Servlets
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.DisabledAccountException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.LockedAccountException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.util.ThreadContext
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter
import org.apache.shiro.web.subject.WebSubject
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import org.slf4j.LoggerFactory
import org.springframework.mobile.device.DeviceResolver
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.URL
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 第三方登录Controller。根据OAuthClient配置，可以支持多个第三方登录。
 */
@Controller
class OAuthController(
        private val credentialsDigest: CredentialsDigest,
        private val captchaService: CaptchaTokenService,
        private val clients: Map<String, OAuthClient>,
        private val userService: UserService,
        private val smsService: SmsService,
        private val imageHandler: ImageHandler,
        private val fileHandler: FileHandler,
        private val deviceResolver: DeviceResolver,
        private val configService: ConfigService
) {
    @GetMapping("/oauth/sign_in/{provider}")
    fun signIn(@PathVariable provider: String, fallbackUrl: String?, request: HttpServletRequest, response: HttpServletResponse, modelMap: Model) {
        val client = getClient(provider)
        val url = client.getAuthorizationUrl(getState(request))
        logger.debug("$provider authorization url: $url")
        request.session.setAttribute(FALLBACK_URL_PARAM, fallbackUrl)
        response.sendRedirect(url)
    }

    @GetMapping("/oauth/callback/{provider}")
    fun callback(@PathVariable provider: String, code: String?, state: String, request: HttpServletRequest, response: HttpServletResponse) {
        if (code == null) throw RuntimeException("code not exist")
        if (!validateState(request, state)) throw RuntimeException("OAuth state illegal: $state")
        val client = getClient(provider)
        val token = client.getOAuthToken(code)
        var user = userService.selectByOpenid(token.provider, token.openid)
        val providerPrefix = getClient(provider).getProviderPrefix()
        val unionid = token.unionid
        if (user == null && (unionid != null && providerPrefix != null)) {
            user = userService.selectByUnionid(providerPrefix, unionid)
            if (user != null) userService.insert(UserOpenid(user.id, provider, token.openid, token.unionid, token.displayName, token.gender, token.pictureUrl))
        }
        if (user == null) {
            // 用户未注册，保存 token 信息，重定向至注册页面。
            request.session.setAttribute(SESSION_OAUTH_TOKEN, token)
            WebUtils.issueRedirect(request, response, OAUTH_SIGN_UP_URL)
        } else {
            // 用户已注册。登录系统。
            authc(provider, user, request, response)
        }
    }

    /**
     * 注册新用户表单
     */
    @GetMapping(OAUTH_SIGN_UP_URL)
    fun signUpForm(request: HttpServletRequest, response: HttpServletResponse, modelMap: Model): String {
        request.setAttribute(SESSION_OAUTH_TOKEN, request.session.getAttribute(SESSION_OAUTH_TOKEN))
        val configs = configService.selectConfigs()
        val theme = configs.getTheme(request, deviceResolver)
        return "$theme/oauth_sign_up"
    }

    /**
     * 注册新用户
     */
    @PostMapping(OAUTH_SIGN_UP_URL)
    fun signUp(username: String, mobile: String?, mobileMessage: String?, mobileMessageId: Long?, request: HttpServletRequest, response: HttpServletResponse, ra: RedirectAttributes): Any? {
        com.jspbb.core.web.api.SignUpController.validateSignUp(username, configs = configService.selectConfigs(), userService = userService)?.let {
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, it.body?.message)
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        val token = request.session.getAttribute(SESSION_OAUTH_TOKEN) as OAuthToken?
        if (token == null) {
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "SessionOauthTokenNotFound")
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        val boundUser = userService.selectByOpenid(token.provider, token.openid)
        if (boundUser != null) {
            // OpenID 已经绑定用户
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "OauthTokenHasBound")
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        val user = User(username = username, ext = UserExt(gender = token.gender, ip = Servlets.getRemoteAddr(request)))
        val userOpenid = UserOpenid(user.id, token.provider, token.openid, token.unionid, token.displayName, token.gender, token.pictureUrl, token.largePictureUrl)
        val configs = configService.selectConfigs()
        // 如果只允许手机注册，则第三方登录也必须绑定手机才可注册，以符合中国法律规定
        if (configs.signUp.verifyMode == VERIFY_MODE_MOBILE) {
            if (smsService.validateMobileMessage(mobileMessage, mobileMessageId, mobile, Sms.USAGE_SIGN_UP)) {
                user.mobile = mobile
            } else {
                return Responses.badRequest("mobileMessage not valid")
            }
        }
        userService.insert(user, user.ext, userOpenid)
        if (!token.largePictureUrl.isNullOrBlank()) {
            FilesEx.getFileFromUrl(URL(token.largePictureUrl), "image")?.let { file ->
                SettingsController.saveProfilePicture(user, file, null, null, null, null, configs, fileHandler, imageHandler, userService)
            }
        }
        authc(token.provider, user, request, response)
        return null
    }

    /**
     * 绑定已有用户
     */
    @PostMapping("/oauth/bind")
    fun bind(username: String, password: String, captcha: String, captchaToken: String, request: HttpServletRequest, response: HttpServletResponse, ra: RedirectAttributes): String? {
        if (!captchaService.validateCaptcha(captchaToken, captcha)) throw RuntimeException("captcha incorrect")
        val user = userService.selectByUsername(username)
        if (user == null) {
            var errorName = UnknownAccountException::class.java.name
            errorName = errorName.substring(errorName.lastIndexOf(".") + 1)
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, errorName)
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        if (!credentialsDigest.matches(user.ext.password, password, user.ext.salt)) {
            var errorName = IncorrectCredentialsException::class.java.name
            errorName = errorName.substring(errorName.lastIndexOf(".") + 1)
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, errorName)
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        val token = request.session.getAttribute(SESSION_OAUTH_TOKEN) as OAuthToken?
        if (token == null) {
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "SessionOauthTokenNotFound")
            return "redirect:/oauth/bind"
        }
        val boundUser = userService.selectByOpenid(token.provider, token.openid)
        if (boundUser != null) {
            // OpenID 已经绑定用户
            ra.addFlashAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, "OauthTokenHasBound")
            return "redirect:$OAUTH_SIGN_UP_URL"
        }
        userService.insert(UserOpenid(user.id, token.provider, token.openid, token.unionid, token.displayName, token.gender, token.pictureUrl))
        authc(token.provider, user, request, response)
        return null
    }

    /**
     * 认证
     */
    private fun authc(provider: String, user: User, request: HttpServletRequest, response: HttpServletResponse) {
        if (user.isLocked()) {
            throw LockedAccountException("user is locked: " + user.username)
        } else if (!user.isNormal()) {
            throw DisabledAccountException("user status unknown: ${user.status}")
        }
        val savedRequest = request.session.getAttribute(WebUtils.SAVED_REQUEST_KEY) as SavedRequest?
        val subject = SecurityUtils.getSubject()
        // 防止session fixation attack(会话固定攻击)，让旧session失效
        subject.getSession(false)?.let { subject.logout() }
        // 前后台登录共用，非管理员也可登录。
        val builder = WebSubject.Builder(request, response)
        val principals = SimplePrincipalCollection(user.username, "oauth_realm_$provider")
        builder.principals(principals).authenticated(true)
        ThreadContext.bind(builder.buildSubject())
        // 将SavedRequest放回session
        request.session.setAttribute(WebUtils.SAVED_REQUEST_KEY, savedRequest)
        val session = request.session
        val fallbackUrl = session.getAttribute(FALLBACK_URL_PARAM) as String?
        session.removeAttribute(FALLBACK_URL_PARAM)
        if (fallbackUrl?.isNotBlank() == true) {
            WebUtils.issueRedirect(request, response, fallbackUrl, null, false)
        } else {
            WebUtils.redirectToSavedRequest(request, response, SUCCESS_URL)
        }
    }

    /**
     * 生成随机 state 代码。
     */
    private fun getState(request: HttpServletRequest): String {
        val state = Digests.randomSecret(32)
        request.session.setAttribute(SESSION_OAUTH_STATE, state)
        return state
    }

    /**
     * 验证state是否和session中的一致。
     */
    private fun validateState(request: HttpServletRequest, state: String): Boolean {
        val sessionState = request.session.getAttribute(SESSION_OAUTH_STATE) as String?
        request.session.removeAttribute(SESSION_OAUTH_STATE)
        return sessionState == state
    }

    private fun getClient(provider: String): OAuthClient = clients[provider + "OAuthClient"] ?: throw RuntimeException("OAuth provider not fount: $provider")

    companion object {
        private val logger = LoggerFactory.getLogger(OAuthController::class.java)
        const val SESSION_OAUTH_TOKEN = "oauthToken"
        const val SESSION_OAUTH_STATE = "oauthState"
        const val SUCCESS_URL = "/"
        const val OAUTH_SIGN_UP_URL = "/oauth/sign_up"
    }
}