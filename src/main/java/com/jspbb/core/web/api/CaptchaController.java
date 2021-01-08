package com.jspbb.core.web.api;

import com.jspbb.core.domain.User;
import com.jspbb.core.service.AccessService;
import com.jspbb.core.service.UserService;
import com.jspbb.core.support.Contexts;
import com.jspbb.util.captcha.CaptchaTokenService;
import com.jspbb.util.captcha.CaptchaTokenService.CaptchaToken;
import com.jspbb.util.web.Servlets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.jspbb.core.Constants.API;
import static com.jspbb.core.Constants.IDENTITY_COOKIE_NAME;

@RestController
@RequestMapping(API)
public class CaptchaController {
    CaptchaController(CaptchaTokenService service, AccessService accessService, UserService userService) {
        this.service = service;
        this.accessService = accessService;
        this.userService = userService;
    }

    @GetMapping("/captcha")
    public CaptchaToken getCaptchaToken(HttpServletRequest request, HttpServletResponse response) {
        accessLog("/captcha", request, response);
        return service.getCaptchaToken();
    }

    @GetMapping("/try_captcha")
    public boolean tryCaptcha(String token, String captcha, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(token) || StringUtils.isBlank(captcha)) return false;
        accessLog("/try_captcha?" + captcha, request, response);
        return service.tryCaptcha(token, captcha);
    }

    private void accessLog(String url, HttpServletRequest request, HttpServletResponse response) {
        User user = null;
        String username = Contexts.getUsername();
        if (StringUtils.isNotBlank(username)) user = userService.selectByUsername(username);
        String ip = Servlets.getRemoteAddr(request);
        String userAgent = request.getHeader("user-agent");
        Long cookie = Servlets.identityCookie(IDENTITY_COOKIE_NAME, request, response);
        accessService.insert(url, null, userAgent, ip, cookie, user);
    }

    private CaptchaTokenService service;
    private AccessService accessService;
    private UserService userService;
}
