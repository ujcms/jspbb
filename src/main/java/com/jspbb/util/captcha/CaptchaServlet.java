package com.jspbb.util.captcha;

import com.jspbb.util.web.Servlets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

/**
 * 验证码Servlet
 *
 * @author liufang
 */
public class CaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_CAPTCHA_SERVICE_ID = "captchaService";
    public static final String CAPTCHA_SERVICE_ID = "captchaServiceId";
    public static final String CAPTCHA_TRY_COUNT_KEY = "_captcha_try_count";
    private TrialImageCaptchaService captchaService;

    @Override
    public void init() throws ServletException {
        String captchaServiceId = getInitParameter(CAPTCHA_SERVICE_ID);
        if (StringUtils.isBlank(captchaServiceId)) {
            captchaServiceId = DEFAULT_CAPTCHA_SERVICE_ID;
        }
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        captchaService = context.getBean(captchaServiceId, TrialImageCaptchaService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String captcha = request.getParameter("captcha");
        if (captcha == null) {
            challenge(request, response);
        } else {
            tryResponse(captcha, request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private void challenge(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");

        try (ServletOutputStream out = response.getOutputStream()) {
            String captchaId = request.getSession().getId();
            // create the image with the text
            BufferedImage challenge = captchaService.getImageChallengeForID(captchaId, Locale.ENGLISH);
            // write the data out
            ImageIO.write(challenge, "jpg", out);
            out.flush();
            // 生成验证码，清空计数
            request.getSession().removeAttribute(CAPTCHA_TRY_COUNT_KEY);
        }
    }

    private void tryResponse(String captcha, HttpServletRequest request, HttpServletResponse response) {
        boolean removeOnError = "true".equals(request.getParameter("removeOnError"));
        HttpSession session = request.getSession(false);
        if (session == null) {
            Servlets.writeJson(response, false);
            return;
        }
        Integer count = (Integer) session.getAttribute(CAPTCHA_TRY_COUNT_KEY);
        if (count == null) {
            count = 0;
        }
        request.getSession().setAttribute(CAPTCHA_TRY_COUNT_KEY, count + 1);
        // 超过尝试次数一律返回 false
        if (count < 50) {
            if (CaptchaUtils.isValidTry(captchaService, request, captcha, removeOnError)) {
                // 验证成功，清空计数
                session.removeAttribute(CAPTCHA_TRY_COUNT_KEY);
                Servlets.writeJson(response, true);
            } else {
                Servlets.writeJson(response, false);
            }
        } else {
            Servlets.writeJson(response, false);
        }
    }
}
